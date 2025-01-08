package fr.nathan818.jarbutcher;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

class EmptyBodyClassVisitor extends ClassVisitor {

    public EmptyBodyClassVisitor(int api) {
        super(api);
    }

    public EmptyBodyClassVisitor(int api, ClassVisitor classVisitor) {
        super(api, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(
        int access,
        String name,
        String descriptor,
        String signature,
        String[] exceptions
    ) {
        return new EmptyBodyMethodVisitor(api, super.visitMethod(access, name, descriptor, signature, exceptions));
    }
}
