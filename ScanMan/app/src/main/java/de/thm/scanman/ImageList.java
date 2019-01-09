package de.thm.scanman;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a List with one fix last element, the addImage.
 * @param <E> The Type the list contains
 * This class is used to display a List in a GridView with a picture of a "+" as last element.
 */
class ImageList<E> {
    private E addImage;
    private List<E> list;

    ImageList(E addImage){
        this.addImage = addImage;
        list = new ArrayList<>();
        list.add(addImage);
    }

    List<E> getList() {
        return list;
    }

    E get (int index){
        if (!validIndex(index)) return null;
        return list.get(index);
    }

    int size(){
        return list.size() - 1;
    }

    void add(E newElement) {
        list.add(size(), newElement);
    }

    E update(int index, E updateElement) {
        if (validIndex(index)) return null;
        return list.set(index, updateElement);
    }

    boolean remove(E updateElement) {
        if (!updateElement.equals(addImage)) return list.remove(updateElement);
        return false;
    }

    boolean remove(int index) {
        if (validIndex(index)) return remove(get(index));
        return false;
    }

    boolean isEmpty(){
        return size() == 0;
    }

    private boolean validIndex(int index){
        return index < 0 || index > size();
    }
}
