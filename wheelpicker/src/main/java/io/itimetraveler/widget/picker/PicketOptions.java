package io.itimetraveler.widget.picker;

/**
 * Created by iTimeTraveler on 2019/3/15.
 */
public class PicketOptions {

    public static final boolean DEFAULT_DIVIDED_EQUALLY = true;
    public static final int DEFAULT_DIVIDER_COLOR = 0xFF333333;
    public static final int DEFAULT_BACKGROUND_COLOR = 0xFFFFFFFF;

    //默认配置
    public static final int DEFAULT_TEXT_SIZE = 20;
    public static final int DEFAULT_TEXT_COLOR = 0xFFAAAAAA;
    public static final int SELECTED_TEXT_COLOR = 0xFF333333;


    // 联动效果
    private boolean linkage;

    // 是否循环
    private boolean cyclic;

    // 多个滚轮宽度是否均等分
    private boolean dividedEqually;

    // 分割线颜色
    private int dividerColor;

    // 背景颜色
    private int backgroundColor;

    private PicketOptions(Builder builder) {
        this.linkage = builder.linkage;
        this.cyclic = builder.cyclic;
        this.dividedEqually = builder.dividedEqually;
        this.dividerColor = builder.dividerColor;
        this.backgroundColor = builder.backgroundColor;
    }

    public boolean isLinkage() {
        return linkage;
    }

    public boolean isCyclic() {
        return cyclic;
    }

    public boolean isDividedEqually() {
        return dividedEqually;
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    public static final class Builder {

        boolean linkage;
        boolean cyclic;
        private boolean dividedEqually = DEFAULT_DIVIDED_EQUALLY;
        private int dividerColor = DEFAULT_DIVIDER_COLOR;
        private int backgroundColor = DEFAULT_BACKGROUND_COLOR;

        public Builder() {
            this.linkage = false;
        }

        private Builder(PicketOptions options) {
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

        public Builder dividedEqually(boolean dividedEqually) {
            this.dividedEqually = dividedEqually;
            return this;
        }

        public Builder dividerColor(int dividerColor) {
            this.dividerColor = dividerColor;
            return this;
        }

        public Builder backgroundColor(int backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public PicketOptions build() {
            return new PicketOptions(this);
        }
    }
}
