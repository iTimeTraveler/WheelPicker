package io.itimetraveler.widget.model;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

/**
 * This class {@PickerNode} organizes data in a hierarchical way. like a Tree or HashMap.
 * Trees are well known as a non-linear Data Structure.
 *
 *             ┌── 15
 *         ┌── 7
 *         │   └── 14
 *     ┌── 3
 *     │   │   ┌── 13
 *     │   └── 6
 *     │       └── 12
 *     1
 *     │       ┌── 11
 *     │   ┌── 5
 *     │   │   └── 10
 *     └── 2
 *         │   ┌── 9
 *         └── 4
 *             └── 8
 */
public class PickerNode<T extends IPickerItemView> {

    @NonNull
    private T data;

    @Nullable
    private List<PickerNode<IPickerItemView>> nextLevel;

    public PickerNode(@NonNull T data) {
        this.data = data;
    }

    public PickerNode(@NonNull T data, @Nullable List<PickerNode<IPickerItemView>> nextLevel) {
        this.nextLevel = nextLevel;
    }

    @NonNull
    public T getData() {
        return data;
    }

    @Nullable
    public List<PickerNode<IPickerItemView>> getNextLevel() {
        return nextLevel;
    }

    public void setNextLevel(@Nullable List<PickerNode<IPickerItemView>> nextLevel) {
        this.nextLevel = nextLevel;
    }

    @Override
    public String toString() {
        return "PickerNode{" +
                "data=" + data +
                ", nextLevel=" + nextLevel +
                '}';
    }
}
