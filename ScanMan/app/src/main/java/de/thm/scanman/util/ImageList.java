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

    public List<E> getList(boolean withAdd) {
        if (withAdd) return list;
        else return list.subList(0, size());
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
        if (!validIndex(index)) return null;
        return list.set(index, updateElement);
    }

    public boolean remove(E updateElement) {
        if (!updateElement.equals(addImage)) return list.remove(updateElement);
        return false;
    }

    private boolean validIndex(int index){
        return index >= 0 && index < size();
    }

    /**
     * Is used to make a selection of the "addImage" impossible
     */
    public void hideAddImage() {
        if (!addImageIsHidden) {
            list.remove(addImage);
            addImageIsHidden = true;
        }
    }
    /**
     * Is used to make a selection of the "addImage" possible
     */
    public void showAddImage() {
        if (addImageIsHidden) {
            list.add(addImage);
            addImageIsHidden = false;
        }
    }
}
