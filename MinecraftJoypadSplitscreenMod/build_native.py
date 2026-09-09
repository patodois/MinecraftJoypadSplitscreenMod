#!/usr/bin/env python3
"""Rebuild the bundled universal macOS JNI library. Requires macOS, Xcode and JDK 8."""
import os
from pathlib import Path
import subprocess
import sys

root = Path(__file__).resolve().parent
if sys.platform != 'darwin':
    raise SystemExit('Rebuilding the Apple GameController bridge requires macOS/Xcode.')
home = os.environ.get('JAVA_HOME')
if not home:
    candidates = sorted((root / 'tools').glob('zulu*/Contents/Home'))
    if not candidates:
        raise SystemExit('Set JAVA_HOME to a JDK with JNI headers.')
    home = str(candidates[-1])
output = root / 'src/main/resources/natives/macos/libjoypadgamecontroller.dylib'
output.parent.mkdir(parents=True, exist_ok=True)
subprocess.run(['clang', '-dynamiclib', '-fobjc-arc', '-arch', 'arm64', '-arch', 'x86_64',
    '-mmacosx-version-min=11.3', '-O2', '-Wall', '-Wextra', '-Wno-unused-parameter',
    '-I', str(Path(home) / 'include'), '-I', str(Path(home) / 'include/darwin'),
    '-framework', 'Foundation', '-framework', 'GameController', '-framework', 'CoreFoundation',
    str(root / 'native/MacGamepad.m'), '-o', str(output)], check=True)
subprocess.run(['codesign', '--force', '--sign', '-', str(output)], check=True)
print(output)
