package it.voxibyte.belt.document.settings.dumper;

import lombok.Getter;
import org.snakeyaml.engine.v2.api.DumpSettings;
import org.snakeyaml.engine.v2.api.DumpSettingsBuilder;
import org.snakeyaml.engine.v2.common.FlowStyle;
import org.snakeyaml.engine.v2.common.NonPrintableStyle;
import org.snakeyaml.engine.v2.common.ScalarStyle;
import org.snakeyaml.engine.v2.common.SpecVersion;
import org.snakeyaml.engine.v2.nodes.Tag;
import org.snakeyaml.engine.v2.resolver.ScalarResolver;
import org.snakeyaml.engine.v2.serializer.AnchorGenerator;
import org.snakeyaml.engine.v2.serializer.NumberAnchorGenerator;

import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;


@SuppressWarnings({"unused", "UnusedReturnValue"})
public class DumperSettings {


    public static final DumperSettings DEFAULT = builder().build();
    private final DumpSettingsBuilder builder;
    private final Supplier<AnchorGenerator> generatorSupplier;
    @Getter
    private final ScalarStyle stringStyle;

    private DumperSettings(Builder builder) {
        this.builder = builder.builder;
        this.generatorSupplier = builder.anchorGeneratorSupplier;
        this.stringStyle = builder.stringStyle;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(DumpSettingsBuilder builder) {
        return new Builder(builder);
    }

    public static Builder builder(DumperSettings settings) {
        return builder(settings.builder).setAnchorGenerator(settings.generatorSupplier);
    }

    public DumpSettings buildEngineSettings() {
        return builder.setAnchorGenerator(generatorSupplier.get()).setDumpComments(true).build();
    }


    public enum Encoding {

        UNICODE,

        ASCII;


        boolean isUnicode() {
            return this == Encoding.UNICODE;
        }
    }

    public static class Builder {


        public static final Supplier<AnchorGenerator> DEFAULT_ANCHOR_GENERATOR = () -> new NumberAnchorGenerator(0);

        public static final FlowStyle DEFAULT_FLOW_STYLE = FlowStyle.BLOCK;

        public static final ScalarStyle DEFAULT_SCALAR_STYLE = ScalarStyle.PLAIN;

        public static final ScalarStyle DEFAULT_STRING_STYLE = ScalarStyle.PLAIN;

        public static final boolean DEFAULT_START_MARKER = false;

        public static final boolean DEFAULT_END_MARKER = false;

        public static final Tag DEFAULT_ROOT_TAG = null;

        public static final boolean DEFAULT_CANONICAL = false;

        public static final boolean DEFAULT_MULTILINE_FORMAT = false;

        public static final Encoding DEFAULT_ENCODING = Encoding.UNICODE;

        public static final int DEFAULT_INDENTATION = 2;

        public static final int DEFAULT_INDICATOR_INDENTATION = 0;

        public static final int DEFAULT_MAX_LINE_WIDTH = 0;

        public static final int DEFAULT_MAX_SIMPLE_KEY_LENGTH = 0;

        public static final boolean DEFAULT_ESCAPE_UNPRINTABLE = true;


        private final DumpSettingsBuilder builder;

        private Supplier<AnchorGenerator> anchorGeneratorSupplier = DEFAULT_ANCHOR_GENERATOR;

        private ScalarStyle stringStyle = DEFAULT_STRING_STYLE;

        private Builder(DumpSettingsBuilder builder) {
            this.builder = builder;
        }


        private Builder() {

            builder = DumpSettings.builder();

            setFlowStyle(DEFAULT_FLOW_STYLE);
            setScalarStyle(DEFAULT_SCALAR_STYLE);
            setStringStyle(DEFAULT_STRING_STYLE);
            setStartMarker(DEFAULT_START_MARKER);
            setEndMarker(DEFAULT_END_MARKER);
            setRootTag(DEFAULT_ROOT_TAG);
            setCanonicalForm(DEFAULT_CANONICAL);
            setMultilineStyle(DEFAULT_MULTILINE_FORMAT);
            setEncoding(DEFAULT_ENCODING);
            setIndentation(DEFAULT_INDENTATION);
            setIndicatorIndentation(DEFAULT_INDICATOR_INDENTATION);
            setLineWidth(DEFAULT_MAX_LINE_WIDTH);
            setMaxSimpleKeyLength(DEFAULT_MAX_SIMPLE_KEY_LENGTH);
            setEscapeUnprintable(DEFAULT_ESCAPE_UNPRINTABLE);
        }


        public Builder setAnchorGenerator(Supplier<AnchorGenerator> generator) {
            this.anchorGeneratorSupplier = generator;
            return this;
        }


        public Builder setFlowStyle(FlowStyle flowStyle) {
            builder.setDefaultFlowStyle(flowStyle);
            return this;
        }


        public Builder setScalarStyle(ScalarStyle scalarStyle) {
            builder.setDefaultScalarStyle(scalarStyle);
            return this;
        }


        public Builder setStringStyle(ScalarStyle stringStyle) {
            this.stringStyle = stringStyle;
            return this;
        }


        public Builder setStartMarker(boolean startMarker) {
            builder.setExplicitStart(startMarker);
            return this;
        }


        public Builder setEndMarker(boolean endMarker) {
            builder.setExplicitEnd(endMarker);
            return this;
        }


        public Builder setScalarResolver(ScalarResolver resolver) {
            builder.setScalarResolver(resolver);
            return this;
        }


        public Builder setRootTag(Tag rootTag) {
            builder.setExplicitRootTag(Optional.ofNullable(rootTag));
            return this;
        }


        public Builder setYamlDirective(SpecVersion directive) {
            builder.setYamlDirective(Optional.ofNullable(directive));
            return this;
        }


        public Builder setTagDirectives(Map<String, String> directives) {
            builder.setTagDirective(directives);
            return this;
        }


        public Builder setCanonicalForm(boolean canonical) {
            builder.setCanonical(canonical);
            return this;
        }


        public Builder setMultilineStyle(boolean multilineStyle) {
            builder.setMultiLineFlow(multilineStyle);
            return this;
        }


        public Builder setEncoding(Encoding encoding) {
            builder.setUseUnicodeEncoding(encoding.isUnicode());
            return this;
        }


        public Builder setIndentation(int spaces) {
            builder.setIndent(spaces);
            return this;
        }


        public Builder setIndicatorIndentation(int spaces) {
            builder.setIndentWithIndicator(spaces > 0);
            builder.setIndicatorIndent(Math.max(spaces, 0));
            return this;
        }


        public Builder setLineWidth(int width) {
            builder.setWidth(width <= 0 ? Integer.MAX_VALUE : width);
            return this;
        }


        public Builder setLineBreak(String lineBreak) {
            builder.setBestLineBreak(lineBreak);
            return this;
        }


        public Builder setMaxSimpleKeyLength(int length) {

            if (length > 1018)
                throw new IllegalArgumentException("Maximum simple key length is limited to 1018!");


            builder.setMaxSimpleKeyLength(length <= 0 ? 1024 : length + 6);
            return this;
        }


        public Builder setEscapeUnprintable(boolean escape) {
            return setUnprintableStyle(escape ? NonPrintableStyle.ESCAPE : NonPrintableStyle.BINARY);
        }


        public Builder setUnprintableStyle(NonPrintableStyle style) {
            builder.setNonPrintableStyle(style);
            return this;
        }


        public DumperSettings build() {
            return new DumperSettings(this);
        }

    }

}
