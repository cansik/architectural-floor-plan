package ch.fhnw.afpars.ui.control

import javafx.scene.control.CheckBoxTreeItem

/**
 * Created by cansik on 16.02.17.
 */
class TagItem(val item : Any? = null, val name : String = "TagItem") {

    override fun toString(): String {
        if(item != null)
            return item.toString()
        else
            return name
    }
}