import java.io.InputStream;
import java.util.*;
import java.util.jar.*;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.*;

/** Checks patched bytecode member references against the actual SRG class hierarchy.
 * This supplements compilation; it is not a Forge launch or a native hardware test.
 */
public class VerifyLinkage {
    static final Map<String, ClassNode> classes = new HashMap<String, ClassNode>();
    static int checked, failed;
    static boolean has(String owner, String name, String desc, boolean field, Set<String> visited) {
        if (owner == null || !visited.add(owner)) return false;
        ClassNode type = classes.get(owner);
        if (type == null) return false;
        if (field) {
            for (Object item : type.fields) {
                FieldNode f = (FieldNode)item;
                if (f.name.equals(name) && f.desc.equals(desc)) return true;
            }
        } else {
            for (Object item : type.methods) {
                MethodNode m = (MethodNode)item;
                if (m.name.equals(name) && m.desc.equals(desc)) return true;
            }
        }
        if (name.equals("<init>")) return false;
        if (has(type.superName, name, desc, field, visited)) return true;
        for (Object iface : type.interfaces) if (has((String)iface, name, desc, field, visited)) return true;
        return false;
    }
    public static void main(String[] args) throws Exception {
        for (String file : args) {
            JarFile jar = new JarFile(file);
            for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();) {
                JarEntry e = entries.nextElement();
                if (!e.getName().endsWith(".class")) continue;
                ClassNode c = new ClassNode();
                InputStream in = jar.getInputStream(e);
                new ClassReader(in).accept(c, 0);
                in.close();
                if (!classes.containsKey(c.name)) classes.put(c.name, c);
            }
            jar.close();
        }
        // Runtime references to java.lang.Object resolve through rt.jar supplied by the caller.
        for (ClassNode c : classes.values()) {
            if (!c.name.startsWith("com/shiny/joypadmod/")) continue;
            if (!System.getProperty("patch.owners", "").contains("|" + c.name.split("\\$")[0] + "|")) continue;
            for (Object entry : c.methods) {
                MethodNode method = (MethodNode)entry;
                for (AbstractInsnNode insn = method.instructions.getFirst(); insn != null; insn = insn.getNext()) {
                    String owner, name, desc; boolean field;
                    if (insn instanceof MethodInsnNode) {
                        MethodInsnNode m = (MethodInsnNode)insn;
                        owner = m.owner; name = m.name; desc = m.desc; field = false;
                    } else if (insn instanceof FieldInsnNode) {
                        FieldInsnNode f = (FieldInsnNode)insn;
                        owner = f.owner; name = f.name; desc = f.desc; field = true;
                    } else continue;
                    if (owner.startsWith("[")) continue;
                    // Validate Minecraft, Forge and the Joypad classes touched by the patch.
                    if (!(owner.startsWith("net/minecraft/") || owner.startsWith("cpw/mods/") || owner.startsWith("net/minecraftforge/") || owner.startsWith("com/shiny/"))) continue;
                    checked++;
                    if (!has(owner, name, desc, field, new HashSet<String>())) {
                        failed++;
                        System.err.println(c.name + ": unresolved " + owner + "." + name + desc);
                    }
                }
            }
        }
        if (failed != 0) throw new AssertionError(failed + " unresolved references");
        System.out.println("PASS: " + checked + " SRG member references resolve in Minecraft/Forge/Joypad class hierarchy.");
    }
}
