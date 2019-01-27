package de.thm.scanman.util;

import java.util.ArrayList;
import java.util.List;

/**
 * This class is a List with one fix last element, the addImage.
 * @param <E> The Type the list contains
 * This class is used to display a List in a GridView with a picture of a "+" as last element.
 */
public class ImageList<E> {
    private E addImage;
    private List<E> list;
    private boolean addImageIsHidden;

    public ImageList(E addImage){
        this.addImage = addImage;
        list = new ArrayList<>();
        list.add(addImage);
    }

    public List<E> getList() {
        return list;
    }

    public E get (int index){
        if (!validIndex(index)) return null;
        return list.get(index);
    }

    public int size(){
        return list.size() - 1;
    }

    public void add(E newElement) {
        list.add(size(), newElement);
    }

    public E update(int index, E updateElement) {
        if (validIndex(index)) return null;
        return list.set(index, updateElement);
    }

    public boolean remove(E updateElement) {
        if (!updateElement.equals(addImage)) return list.remove(updateElement);
        return false;
    }

    public boolean remove(int index) {
        if (validIndex(index)) return remove(get(index));
        return false;
    }

    public boolean isEmpty(){
        return size() == 0;
    }

    private boolean validIndex(int index){
        return index < 0 || index > size();
    }

    public void hideAddImage() {
        if (!addImageIsHidden) {
            list.remove(addImage);
            addImageIsHidden = true;
        }
    }

    public void showAddImage() {
        if (addImageIsHidden) {
            list.add(addImage);
            addImageIsHidden = false;
        }
    }
}
