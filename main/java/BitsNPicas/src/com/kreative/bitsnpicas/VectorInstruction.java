package com.kreative.bitsnpicas;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class VectorInstruction {
	private final char operation;
	private final Number[] operands;
	
	public VectorInstruction(char operation, Number... operands) {
		this.operation = operation;
		this.operands = new Number[operands.length];
		int i = 0; for (Number n : operands) this.operands[i++] = n;
	}
	
	public VectorInstruction(char operation, Collection<? extends Number> operands) {
		this.operation = operation;
		this.operands = new Number[operands.size()];
		int i = 0; for (Number n : operands) this.operands[i++] = n;
	}
	
	public char getOperation() {
		return operation;
	}
	
	public List<Number> getOperands() {
		return Collections.unmodifiableList(Arrays.asList(operands));
	}
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(operation);
		for (Number n : operands) {
			sb.append(" ");
			sb.append(n);
		}
		return sb.toString();
	}
}
