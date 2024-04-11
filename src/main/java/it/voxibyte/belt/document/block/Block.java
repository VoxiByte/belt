package it.voxibyte.belt.document.block;

import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import org.snakeyaml.engine.v2.comments.CommentLine;
import org.snakeyaml.engine.v2.comments.CommentType;
import org.snakeyaml.engine.v2.nodes.MappingNode;
import org.snakeyaml.engine.v2.nodes.Node;
import org.snakeyaml.engine.v2.nodes.NodeTuple;
import org.snakeyaml.engine.v2.nodes.SequenceNode;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@SuppressWarnings({"unused", "UnusedReturnValue"})
public abstract class Block<T> {


    private final T value;
    List<CommentLine> beforeKeyComments = new ArrayList<>(0), inlineKeyComments = null, afterKeyComments = null, beforeValueComments = null, inlineValueComments = null, afterValueComments = null;
    @Getter
    private boolean ignored;


    public Block(Node keyNode, Node valueNode, T value) {
        this.value = value;
        init(keyNode, valueNode);
    }


    public Block(T value) {
        this(null, null, value);
    }


    public Block(Block<?> previous, T value) {

        this.value = value;

        if (previous == null)
            return;


        this.beforeKeyComments = previous.beforeKeyComments;
        this.inlineKeyComments = previous.inlineKeyComments;
        this.afterKeyComments = previous.afterKeyComments;
        this.beforeValueComments = previous.beforeValueComments;
        this.inlineValueComments = previous.inlineValueComments;
        this.afterValueComments = previous.afterValueComments;
    }


    protected void init(Node key, Node value) {

        if (key != null) {

            beforeKeyComments = key.getBlockComments() == null ? new ArrayList<>(0) : key.getBlockComments();

            if (key.getInLineComments() != null)
                beforeKeyComments.addAll(toBlockComments(key.getInLineComments()));
            if (key.getEndComments() != null)
                beforeKeyComments.addAll(toBlockComments(key.getEndComments()));

            collectComments(key, true);
        }


        if (value != null) {

            beforeValueComments = value.getBlockComments();

            if (beforeKeyComments == null)
                beforeKeyComments = new ArrayList<>(0);

            if (value.getInLineComments() != null)
                beforeKeyComments.addAll(toBlockComments(value.getInLineComments()));
            if (value.getEndComments() != null)
                beforeKeyComments.addAll(toBlockComments(value.getEndComments()));

            collectComments(value, true);
        }
    }


    private void collectComments(Node node, boolean initial) {

        if (!initial) {
            if (node.getBlockComments() != null)
                beforeKeyComments.addAll(toBlockComments(node.getBlockComments()));
            if (node.getInLineComments() != null)
                beforeKeyComments.addAll(toBlockComments(node.getInLineComments()));
            if (node.getEndComments() != null)
                beforeKeyComments.addAll(toBlockComments(node.getEndComments()));
        } else {

            if (beforeKeyComments == null)
                beforeKeyComments = new ArrayList<>(0);
        }


        if (node instanceof SequenceNode sequenceNode) {

            for (Node sub : sequenceNode.getValue())

                collectComments(sub, false);
        } else if (!initial && node instanceof MappingNode mappingNode) {

            for (NodeTuple sub : mappingNode.getValue()) {

                collectComments(sub.getKeyNode(), false);
                collectComments(sub.getValueNode(), false);
            }
        }
    }


    private List<CommentLine> toBlockComments(List<CommentLine> commentLines) {

        int i = -1;

        for (CommentLine commentLine : commentLines)
            commentLines.set(++i, commentLine.getCommentType() != CommentType.IN_LINE ? commentLine : new CommentLine(commentLine.getStartMark(), commentLine.getEndMark(), commentLine.getValue(), CommentType.BLOCK));

        return commentLines;
    }


    @Nullable
    public List<String> getComments() {

        List<CommentLine> comments = Comments.get(this, Comments.NodeType.KEY, Comments.Position.BEFORE);

        if (comments == null)
            return null;


        return comments.stream().map(CommentLine::getValue).collect(Collectors.toList());
    }


    public void setComments(List<String> comments) {
        Comments.set(this, Comments.NodeType.KEY, Comments.Position.BEFORE, comments == null ? null : comments.stream().map(comment -> Comments.create(comment, Comments.Position.BEFORE)).collect(Collectors.toList()));
    }


    public void removeComments() {
        Comments.remove(this, Comments.NodeType.KEY, Comments.Position.BEFORE);
    }


    public void addComments(List<String> comments) {
        Comments.add(this, Comments.NodeType.KEY, Comments.Position.BEFORE, comments.stream().map(comment -> Comments.create(comment, Comments.Position.BEFORE)).collect(Collectors.toList()));
    }


    public void addComment(String comment) {
        Comments.add(this, Comments.NodeType.KEY, Comments.Position.BEFORE, Comments.create(comment, Comments.Position.BEFORE));
    }

    public void setIgnored(boolean ignored) {
        this.ignored = ignored;
    }

    public abstract boolean isSection();


    public T getStoredValue() {
        return value;
    }
}
