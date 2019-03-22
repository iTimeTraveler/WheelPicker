package io.itimetraveler.widget.picker;

/**
 * Created by iTimeTraveler on 2019/3/15.
 */
public class PicketOptions {

    public static final int DEFAULT_DIVIDER_COLOR = 0xFF333333;

    // 联动效果
    boolean linkage;

    // 是否循环
    boolean cyclic;

    // 分割线颜色
    int dividerColor;

    private PicketOptions(Builder builder) {
        this.linkage = builder.linkage;
        this.cyclic = builder.cyclic;
        this.dividerColor = builder.dividerColor;
    }

    public boolean isLinkage() {
        return linkage;
    }

    public boolean isCyclic() {
        return cyclic;
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public static final class Builder {

        boolean linkage;
        boolean cyclic;
        int dividerColor = DEFAULT_DIVIDER_COLOR;

        public Builder() {
            this.linkage = false;
        }

        public Builder(PicketOptions options) {
            this.linkage = options.linkage;
            this.cyclic = options.cyclic;
        }

        public Builder linkage(boolean linkage) {
            this.linkage = linkage;
            return this;
        }

        public Builder cyclic(boolean cyclic) {
            this.cyclic = cyclic;
            return this;
        }

        public Builder dividerColor(int dividerColor) {
            this.dividerColor = dividerColor;
            return this;
        }

        public PicketOptions build() {
            return new PicketOptions(this);
        }
    }
}
