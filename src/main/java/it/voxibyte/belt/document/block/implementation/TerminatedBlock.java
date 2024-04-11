package it.voxibyte.belt.document.block.implementation;

import it.voxibyte.belt.document.block.Block;
import org.snakeyaml.engine.v2.nodes.Node;


public class TerminatedBlock extends Block<Object> {


    public TerminatedBlock(Node keyNode, Node valueNode, Object value) {
        super(keyNode, valueNode, value);
    }


    public TerminatedBlock(Block<?> previous, Object value) {
        super(previous, value);
    }

    @Override

    public boolean isSection() {
        return false;
    }
}
