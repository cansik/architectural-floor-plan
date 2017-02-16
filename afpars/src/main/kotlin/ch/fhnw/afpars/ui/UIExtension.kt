package ch.fhnw.afpars.ui

import ch.fhnw.afpars.ui.control.TagItem
import javafx.scene.control.TreeItem
import javafx.scene.control.TreeView

/**
 * Created by cansik on 16.02.17.
 */


fun TreeView<TagItem>.items(current : TreeItem<TagItem> = this.root,
                         items : MutableList<TreeItem<TagItem>> = mutableListOf<TreeItem<TagItem>>())
        : MutableList<TreeItem<TagItem>>
{
    items.add(current)

    current.children.forEach {
        this.items(it, items)
    }

    return items
}