package xyz.breversed.detectors.bozar;

import xyz.breversed.api.detection.AbstractDetector;

import java.util.concurrent.atomic.AtomicBoolean;

public class BozarFlow extends AbstractDetector {

    @Override
    protected boolean detect() {
        final AtomicBoolean detected = new AtomicBoolean(false);
        getClasses().forEach(classNode -> classNode.methods.forEach(methodNode -> methodNode.instructions.forEach(abstractInsnNode -> {
            if (abstractInsnNode.getOpcode() == LDC && getNext(abstractInsnNode, 1).getOpcode() == I2L &&
                    getNext(abstractInsnNode, 2).getOpcode() == LDC &&
                    getNext(abstractInsnNode, 3).getOpcode() == LXOR && getNext(abstractInsnNode, 4).getOpcode() == LDC &&
                    getNext(abstractInsnNode, 5).getOpcode() == I2L && getNext(abstractInsnNode, 6).getOpcode() == LDC &&
                    getNext(abstractInsnNode, 7).getOpcode() == LXOR && getNext(abstractInsnNode, 8).getOpcode() == LXOR &&
                    getNext(abstractInsnNode, 9).getOpcode() == LDIV && getNext(abstractInsnNode, 10).getOpcode() == GOTO) {
                detected.set(true);
            }
        })));

        return detected.get();
    }
}
