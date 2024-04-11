package it.voxibyte.belt.document.settings.updater;

import it.voxibyte.belt.document.block.Block;
import it.voxibyte.belt.document.block.implementation.Section;
import it.voxibyte.belt.document.route.Route;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Function;

public interface ValueMapper {

    static ValueMapper section(BiFunction<Section, Route, Object> mapper) {
        return new ValueMapper() {
            @Override

            public Object map(Section section, Route key) {
                return mapper.apply(section, key);
            }
        };
    }

    static ValueMapper block(Function<Block<?>, Object> mapper) {
        return new ValueMapper() {
            @Override

            public Object map(Block<?> block) {
                return mapper.apply(block);
            }
        };
    }

    static ValueMapper value(Function<Object, Object> mapper) {
        return new ValueMapper() {
            @Override

            public Object map(Object value) {
                return mapper.apply(value);
            }
        };
    }

    @Nullable
    default Object map(Section section, Route key) {
        return map(section.getBlock(key));
    }

    @Nullable
    default Object map(Block<?> block) {
        return map(block.getStoredValue());
    }

    @Nullable
    default Object map(Object value) {
        return value;
    }

}
