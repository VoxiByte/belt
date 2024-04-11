package it.voxibyte.belt.document.block;

import org.jetbrains.annotations.Nullable;
import org.snakeyaml.engine.v2.comments.CommentLine;
import org.snakeyaml.engine.v2.comments.CommentType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class Comments {


    public static final CommentLine BLANK_LINE = new CommentLine(Optional.empty(), Optional.empty(), "", CommentType.BLANK_LINE);

    @Nullable
    public static List<CommentLine> get(Block<?> block, NodeType node, Position position) {
        switch (position) {
            case BEFORE:
                return node == NodeType.KEY ? block.beforeKeyComments : block.beforeValueComments;
            case INLINE:
                return node == NodeType.KEY ? block.inlineKeyComments : block.inlineValueComments;
            case AFTER:
                return node == NodeType.KEY ? block.afterKeyComments : block.afterValueComments;
            default:
                return null;
        }
    }

    public static void set(Block<?> block, NodeType node, Position position, List<CommentLine> comments) {

        if (comments != null)
            comments = new ArrayList<>(comments);

        switch (position) {
            case BEFORE:
                if (node == NodeType.KEY)
                    block.beforeKeyComments = comments;
                else
                    block.beforeValueComments = comments;
                break;
            case INLINE:
                if (node == NodeType.KEY)
                    block.inlineKeyComments = comments;
                else
                    block.inlineValueComments = comments;
                break;
            case AFTER:
                if (node == NodeType.KEY)
                    block.afterKeyComments = comments;
                else
                    block.afterValueComments = comments;
                break;
        }
    }

    public static void remove(Block<?> block, NodeType node, Position position) {
        set(block, node, position, null);
    }

    public static void add(Block<?> block, NodeType node, Position position, List<CommentLine> comments) {
        comments.forEach(comment -> add(block, node, position, comment));
    }

    public static void add(Block<?> block, NodeType node, Position position, CommentLine comment) {
        switch (position) {
            case BEFORE:
                if (node == NodeType.KEY) {

                    if (block.beforeKeyComments == null)
                        block.beforeKeyComments = new ArrayList<>();

                    block.beforeKeyComments.add(comment);
                } else {

                    if (block.beforeValueComments == null)
                        block.beforeValueComments = new ArrayList<>();

                    block.beforeValueComments.add(comment);
                }
                break;
            case INLINE:
                if (node == NodeType.KEY) {

                    if (block.inlineKeyComments == null)
                        block.inlineKeyComments = new ArrayList<>();

                    block.inlineKeyComments.add(comment);
                } else {

                    if (block.inlineValueComments == null)
                        block.inlineValueComments = new ArrayList<>();

                    block.inlineValueComments.add(comment);
                }
                break;
            case AFTER:
                if (node == NodeType.KEY) {

                    if (block.afterKeyComments == null)
                        block.afterKeyComments = new ArrayList<>();

                    block.afterKeyComments.add(comment);
                } else {

                    if (block.afterValueComments == null)
                        block.afterValueComments = new ArrayList<>();

                    block.afterValueComments.add(comment);
                }
                break;
        }
    }

    public static CommentLine create(String comment, Position position) {
        return new CommentLine(Optional.empty(), Optional.empty(), comment, position == Position.INLINE ? CommentType.IN_LINE : CommentType.BLOCK);
    }


    public enum Position {

        BEFORE,


        INLINE,


        AFTER
    }


    public enum NodeType {

        KEY,

        VALUE
    }

}
