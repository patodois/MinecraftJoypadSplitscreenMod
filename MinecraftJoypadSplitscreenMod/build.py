#!/usr/bin/env python3
"""Build a targeted, SRG-mapped patch of the supplied Joypad JAR. Python 3 + JDK 8.
No Minecraft/Forge classes or build tools are added to the delivered mod.
"""
import argparse
import csv
import hashlib
import io
import json
import os
from pathlib import Path
import shutil
import subprocess
import zipfile

ROOT = Path(__file__).resolve().parent
os.chdir(ROOT)
P = ROOT / 'tools'
B = ROOT / 'build'
LOCK = json.loads((ROOT / 'build-lock.json').read_text())


def run(*args):
    subprocess.run([str(x) for x in args], check=True)


def digest(path):
    return hashlib.sha256(path.read_bytes()).hexdigest()


def merge(output, inputs, classes_only=True):
    seen = set()
    with zipfile.ZipFile(output, 'w', zipfile.ZIP_DEFLATED) as out:
        for path in inputs:
            if path.is_dir():
                entries = [(p.relative_to(path).as_posix(), p.read_bytes()) for p in sorted(path.rglob('*')) if p.is_file()]
            else:
                with zipfile.ZipFile(path) as source:
                    entries = [(n, source.read(n)) for n in source.namelist() if not n.endswith('/')]
            for name, data in entries:
                if name not in seen and (not classes_only or name.endswith('.class')):
                    out.writestr(name, data)
                    seen.add(name)


def main():
    parser = argparse.ArgumentParser()
    parser.add_argument('--original', type=Path, default=Path.home() / 'Downloads' / LOCK['original']['filename'])
    args = parser.parse_args()
    original = args.original.resolve()
    if not original.is_file() or digest(original) != LOCK['original']['sha256']:
        raise SystemExit('Supply the exact original JAR with --original PATH (see build-lock.json).')
    P.mkdir(exist_ok=True)
    B.mkdir(exist_ok=True)
    java_home = os.environ.get('JAVA_HOME')
    if not java_home:
        matches = sorted(P.glob('zulu*/Contents/Home'))
        if matches:
            java_home = str(matches[-1])
    executable = '.exe' if os.name == 'nt' else ''
    java = Path(java_home) / 'bin' / ('java' + executable) if java_home else shutil.which('java')
    javac = Path(java_home) / 'bin' / ('javac' + executable) if java_home else shutil.which('javac')
    if not java or not javac:
        raise SystemExit('Set JAVA_HOME to a JDK 8 installation.')
    version = subprocess.run([str(javac), '-version'], capture_output=True, text=True, check=True)
    if '1.8.' not in version.stdout + version.stderr:
        raise SystemExit('This legacy build requires JDK 8. Set JAVA_HOME to JDK 8.')
    for name, item in LOCK['dependencies'].items():
        target = P / name
        if not target.is_file() or digest(target) != item['sha256']:
            run('curl', '-fL', '--retry', '2', item['url'], '-o', target)
        if digest(target) != item['sha256']:
            raise SystemExit('Checksum mismatch: ' + name)
    with zipfile.ZipFile(P / 'mcp-srg.zip') as archive:
        (P / 'joined.srg').write_bytes(archive.read('joined.srg'))
    with zipfile.ZipFile(P / 'forge-userdev.jar') as archive:
        (P / 'forge-binaries.jar').write_bytes(archive.read('binaries.jar'))
    names = {}
    with zipfile.ZipFile(P / 'mcp-names.zip') as archive:
        for name in ['fields.csv', 'methods.csv']:
            for row in csv.DictReader(io.StringIO(archive.read(name).decode())):
                names[row['searge']] = row['name']
    mapping = []
    for line in (P / 'joined.srg').read_text().splitlines():
        a = line.split()
        if a[0] == 'CL:':
            mapping.append('CL: ' + a[2] + ' ' + a[2])
        elif a[0] == 'FD:':
            owner, name = a[2].rsplit('/', 1)
            mapping.append('FD: ' + a[2] + ' ' + owner + '/' + names.get(name, name))
        elif a[0] == 'MD:':
            owner, name = a[3].rsplit('/', 1)
            mapping.append('MD: ' + a[3] + ' ' + a[4] + ' ' + owner + '/' + names.get(name, name) + ' ' + a[4])
    (P / 'srg-mcp.srg').write_text('\n'.join(mapping) + '\n')

    def remap(source, target, mappings, reverse=False):
        extra = ['--reverse'] if reverse else []
        run(java, '-jar', P / 'specialsource.jar', '-i', source, '-o', target, '-m', mappings, '-q', *extra)

    remap(P / 'minecraft-client.jar', P / 'minecraft-srg.jar', P / 'joined.srg')
    remap(P / 'forge-binaries.jar', P / 'forge-srg.jar', P / 'joined.srg')
    merge(P / 'compile-srg.jar', [original, P / 'forge-srg.jar', P / 'minecraft-srg.jar'])
    remap(P / 'compile-srg.jar', P / 'compile-mcp.jar', P / 'srg-mcp.srg')
    dependencies = [P / name for name in ['compile-mcp.jar', 'log4j-api.jar', 'log4j-core.jar', 'guava.jar', 'authlib.jar', 'commons-lang3.jar']]
    cp = os.pathsep.join(str(x) for x in dependencies)
    classes = B / 'classes'
    if classes.exists():
        shutil.rmtree(classes)
    classes.mkdir()
    patch = json.loads((ROOT / 'patch-manifest.json').read_text())
    sources = [ROOT / 'src/main/java' / name for name in patch['sources']]
    run(javac, '-encoding', 'UTF-8', '-source', '7', '-target', '7', '-cp', cp, '-d', classes, *sources)
    (B / 'tests').mkdir(exist_ok=True)
    run(javac, '-encoding', 'UTF-8', '-cp', str(classes) + os.pathsep + cp, '-d', B / 'tests', ROOT / 'tests/InputRegressionTest.java')
    run(java, '-Xverify:all', '-cp', str(B / 'tests') + os.pathsep + str(classes) + os.pathsep + cp, 'com.shiny.joypadmod.devices.InputRegressionTest')
    # Include the hierarchy while remapping so inherited GuiScreen/GuiButton methods
    # and field references also receive the correct runtime SRG names.
    merge(B / 'patched-mcp.jar', [classes, P / 'compile-mcp.jar'])
    remap(B / 'patched-mcp.jar', B / 'patched-srg.jar', P / 'srg-mcp.srg', reverse=True)
    changed = {p.relative_to(classes).as_posix() for p in classes.rglob('*.class')}
    owners = {p.relative_to(ROOT / 'src/main/java').as_posix()[:-5] for p in sources}
    resources = ROOT / 'src/main/resources'
    entries = {}
    with zipfile.ZipFile(original) as archive:
        for name in archive.namelist():
            if name.endswith('/'):
                continue
            owner = name[:-6].split('$')[0] if name.endswith('.class') else ''
            if owner not in owners:
                entries[name] = archive.read(name)
    with zipfile.ZipFile(B / 'patched-srg.jar') as archive:
        for name in changed:
            entries[name] = archive.read(name)
    for name in patch['resources']:
        entries[name] = (resources / name).read_bytes()
    dist = ROOT / 'dist'
    dist.mkdir(exist_ok=True)
    output = dist / 'JoypadMod-1.7.10-Enhanced-0.2.0.jar'
    with zipfile.ZipFile(output, 'w', zipfile.ZIP_DEFLATED) as archive:
        for name, data in sorted(entries.items()):
            info = zipfile.ZipInfo(name, date_time=(1980, 1, 1, 0, 0, 0))
            info.compress_type = zipfile.ZIP_DEFLATED
            archive.writestr(info, data)
    (dist / 'SHA256SUMS.txt').write_text(digest(output) + '  ' + output.name + '\n')
    (B / 'patched-classes.json').write_text(json.dumps(sorted(changed), indent=2))
    run(javac, '-cp', P / 'specialsource.jar', '-d', B / 'tests', ROOT / 'tests/VerifyLinkage.java')
    jdk = Path(java_home) if java_home else Path(javac).resolve().parent.parent
    run(java, '-Dpatch.owners=|' + '|'.join(sorted(owners)) + '|', '-cp',
        str(B / 'tests') + os.pathsep + str(P / 'specialsource.jar'), 'VerifyLinkage',
        output, P / 'forge-srg.jar', P / 'minecraft-srg.jar', jdk / 'jre/lib/rt.jar')
    print('Built:', output)
    print('SHA256:', digest(output))


if __name__ == '__main__':
    main()
