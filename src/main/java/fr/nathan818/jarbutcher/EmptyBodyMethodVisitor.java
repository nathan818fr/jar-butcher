package fr.nathan818.jarbutcher;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

class EmptyBodyMethodVisitor extends MethodVisitor {

    protected MethodVisitor target;

    public EmptyBodyMethodVisitor(int api, MethodVisitor methodVisitor) {
        super(api);
        this.target = methodVisitor;
    }

    @Override
    public void visitParameter(String name, int access) {
        target.visitParameter(name, access);
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return target.visitAnnotationDefault();
    }

    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        return target.visitAnnotation(descriptor, visible);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return target.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    @Override
    public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
        target.visitAnnotableParameterCount(parameterCount, visible);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        return target.visitParameterAnnotation(parameter, descriptor, visible);
    }

    @Override
    public void visitAttribute(Attribute attribute) {
        target.visitAttribute(attribute);
    }

    @Override
    public void visitCode() {
        target.visitCode();
        target.visitInsn(Opcodes.RETURN);
    }

    @Override
    public void visitMaxs(int maxStack, int maxLocals) {
        target.visitMaxs(maxStack, maxLocals);
    }

    @Override
    public void visitEnd() {
        target.visitEnd();
    }
}
