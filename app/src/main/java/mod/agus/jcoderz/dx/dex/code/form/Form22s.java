package mod.agus.jcoderz.dx.dex.code.form;

import java.util.BitSet;
import mod.agus.jcoderz.dx.dex.code.CstInsn;
import mod.agus.jcoderz.dx.dex.code.DalvInsn;
import mod.agus.jcoderz.dx.dex.code.InsnFormat;
import mod.agus.jcoderz.dx.rop.code.RegisterSpecList;
import mod.agus.jcoderz.dx.rop.cst.Constant;
import mod.agus.jcoderz.dx.rop.cst.CstLiteralBits;
import mod.agus.jcoderz.dx.util.AnnotatedOutput;

public final class Form22s extends InsnFormat {
    public static final InsnFormat THE_ONE = new Form22s();

    private Form22s() {
    }

    @Override // mod.agus.jcoderz.dx.dex.code.InsnFormat
    public String insnArgString(DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        return String.valueOf(registers.get(0).regString()) + ", " + registers.get(1).regString() + ", " + literalBitsString((CstLiteralBits) ((CstInsn) dalvInsn).getConstant());
    }

    @Override // mod.agus.jcoderz.dx.dex.code.InsnFormat
    public String insnCommentString(DalvInsn dalvInsn, boolean z) {
        return literalBitsComment((CstLiteralBits) ((CstInsn) dalvInsn).getConstant(), 16);
    }

    @Override // mod.agus.jcoderz.dx.dex.code.InsnFormat
    public int codeSize() {
        return 2;
    }

    @Override // mod.agus.jcoderz.dx.dex.code.InsnFormat
    public boolean isCompatible(DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        if (!(dalvInsn instanceof CstInsn) || registers.size() != 2 || !unsignedFitsInNibble(registers.get(0).getReg()) || !unsignedFitsInNibble(registers.get(1).getReg())) {
            return false;
        }
        Constant constant = ((CstInsn) dalvInsn).getConstant();
        if (!(constant instanceof CstLiteralBits)) {
            return false;
        }
        CstLiteralBits cstLiteralBits = (CstLiteralBits) constant;
        return cstLiteralBits.fitsInInt() && signedFitsInShort(cstLiteralBits.getIntBits());
    }

    @Override // mod.agus.jcoderz.dx.dex.code.InsnFormat
    public BitSet compatibleRegs(DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        BitSet bitSet = new BitSet(2);
        bitSet.set(0, unsignedFitsInNibble(registers.get(0).getReg()));
        bitSet.set(1, unsignedFitsInNibble(registers.get(1).getReg()));
        return bitSet;
    }

    @Override // mod.agus.jcoderz.dx.dex.code.InsnFormat
    public void writeTo(AnnotatedOutput annotatedOutput, DalvInsn dalvInsn) {
        RegisterSpecList registers = dalvInsn.getRegisters();
        write(annotatedOutput, opcodeUnit(dalvInsn, makeByte(registers.get(0).getReg(), registers.get(1).getReg())), (short) ((CstLiteralBits) ((CstInsn) dalvInsn).getConstant()).getIntBits());
    }
}
