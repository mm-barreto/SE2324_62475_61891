package net.sf.freecol.client.gui.action;
import java.awt.event.ActionEvent;

import net.sf.freecol.client.FreeColClient;

public class TutorialAction extends FreeColAction{

    public static final String id = "TutorialAction";

    public TutorialAction(FreeColClient freeColClient){
        super(freeColClient, id);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        getGUI().showTutorialMissionPanel();
    }
}
