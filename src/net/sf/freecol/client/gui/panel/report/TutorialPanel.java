package net.sf.freecol.client.gui.panel.report;

import java.awt.*;

import javax.swing.JLabel;

import net.miginfocom.swing.MigLayout;
import net.sf.freecol.client.FreeColClient;
import net.sf.freecol.client.gui.FontLibrary;
import net.sf.freecol.client.gui.panel.BuildingPanel;
import net.sf.freecol.client.gui.panel.FreeColProgressBar;
import net.sf.freecol.client.gui.panel.Utility;
import net.sf.freecol.common.model.Building;
import net.sf.freecol.common.model.Colony;
import net.sf.freecol.common.model.GoodsType;
import net.sf.freecol.common.model.Player;
import net.sf.freecol.common.model.Specification;
import net.sf.freecol.common.model.WorkLocation;

import static net.sf.freecol.common.util.CollectionUtils.*;

public class TutorialPanel extends ReportPanel {
    public TutorialPanel(FreeColClient freeColClient) {
        super(freeColClient, "TutorialAction");

        final Font font = FontLibrary.getScaledFont("normal-bold-smaller");
        final Player player = getMyPlayer();
        final Specification spec = getSpecification();
        final int MAXMISSIONS = 11;
        final int MINMISSIONS = 0;
        final int STEP = 1;
        int counter = 0;

        reportPanel.setLayout(new MigLayout("wrap 6, fill", "center"));

        // Add text for settling a new colony mission
        //change in move action
        JLabel settleMissionLabel = new JLabel("Mission 1:");
        settleMissionLabel.setFont(font);
        reportPanel.add(settleMissionLabel, SPAN_SPLIT_2);

        JLabel settleMissionDescription = new JLabel("Move an unit.");
        if(player.gethasMoved()){
            counter++;
            settleMissionDescription.setForeground(Color.GREEN);
        }else{
            settleMissionDescription.setForeground(Color.RED);
        }
        settleMissionDescription.setForeground(player.gethasMoved() ? Color.GREEN : Color.RED);
        settleMissionDescription.setFont(font);
        reportPanel.add(settleMissionDescription, "span");

        JLabel settleMission2 = new JLabel("Mission 2:");
        settleMission2.setFont(font);
        reportPanel.add(settleMission2, SPAN_SPLIT_2);

        JLabel settleMission2Description = new JLabel("Disembark an unit.");
        if(player.gethasDisembarked()){
            counter++;
            settleMission2Description.setForeground(Color.GREEN);
        }else{
            settleMission2Description.setForeground(Color.RED);
        }
        settleMission2Description.setFont(font);
        reportPanel.add(settleMission2Description, "span");

        JLabel settleMission3 = new JLabel("Mission 3:");
        settleMission3.setFont(font);
        reportPanel.add(settleMission3, SPAN_SPLIT_2);

        JLabel settleMission3Description = new JLabel("End a turn.");
        if(player.gethasendTurn()){
            counter++;
            settleMission3Description.setForeground(Color.GREEN);
        }else{
            settleMission3Description.setForeground(Color.RED);
        }
        settleMission3Description.setFont(font);
        reportPanel.add(settleMission3Description, "span");

        JLabel settleMission4 = new JLabel("Mission 4:");
        settleMission4.setFont(font);
        reportPanel.add(settleMission4, SPAN_SPLIT_2);

        JLabel settleMission4Description = new JLabel("Explore and find a suitable spot for your colony.");
        if(!player.getSettlementList().isEmpty()){
            counter++;
            settleMission4Description.setForeground(Color.GREEN);
        }else{
            settleMission4Description.setForeground(Color.RED);
        }
        settleMission4Description.setFont(font);
        reportPanel.add(settleMission4Description, "span");

        JLabel settleMission5 = new JLabel("Mission 5:");
        settleMission5.setFont(font);
        reportPanel.add(settleMission5, SPAN_SPLIT_2);

        JLabel settleMission5Description = new JLabel("Create a Port in a settlement.");
        if(player.getNumberOfPorts() != 0){
            counter++;
            settleMission5Description.setForeground(Color.GREEN);
        }else{
            settleMission5Description.setForeground(Color.RED);
        }
        settleMission5Description.setFont(font);
        reportPanel.add(settleMission5Description, "span");

        JLabel settleMission6 = new JLabel("Mission 6:");
        settleMission6.setFont(font);
        reportPanel.add(settleMission6, SPAN_SPLIT_2);

        JLabel settleMission6Description = new JLabel("Explore a Lost Cities rumor.");
        if(player.gethasExpRumours()){
            counter++;
            settleMission6Description.setForeground(Color.GREEN);
        }else{
            settleMission6Description.setForeground(Color.RED);
        }
        settleMission6Description.setFont(font);
        reportPanel.add(settleMission6Description, "span");

        JLabel settleMission7 = new JLabel("Mission 7:");
        settleMission7.setFont(font);
        reportPanel.add(settleMission7, SPAN_SPLIT_2);

        JLabel settleMission7Description = new JLabel("Put a unit living with natives.");
        if(player.gethasLearnSkill()){
            counter++;
            settleMission7Description.setForeground(Color.GREEN);
        }else{
            settleMission7Description.setForeground(Color.RED);
        }
        settleMission7Description.setFont(font);
        reportPanel.add(settleMission7Description, "span");

        JLabel settleMission8 = new JLabel("Mission 8:");
        settleMission8.setFont(font);
        reportPanel.add(settleMission8, SPAN_SPLIT_2);

        JLabel settleMission8Description = new JLabel("Have a first contact with a native.");
        if(player.gethasfirstContact()){
            counter++;
            settleMission8Description.setForeground(Color.GREEN);
        }else{
            settleMission8Description.setForeground(Color.RED);
        }
        settleMission8Description.setFont(font);
        reportPanel.add(settleMission8Description, "span");

        JLabel settleMission9 = new JLabel("Mission 9:");
        settleMission9.setFont(font);
        reportPanel.add(settleMission9, SPAN_SPLIT_2);

        JLabel settleMission9Description = new JLabel("Sell products to Europe.");
        if(player.gethassellGoods()){
            counter++;
            settleMission9Description.setForeground(Color.GREEN);
        }else{
            settleMission9Description.setForeground(Color.RED);
        }
        settleMission9Description.setFont(font);
        reportPanel.add(settleMission9Description, "span");

        JLabel settleMission10 = new JLabel("Mission 10:");
        settleMission10.setFont(font);
        reportPanel.add(settleMission10, SPAN_SPLIT_2);

        JLabel settleMission10Description = new JLabel("Recruit a unit from Europe.");
        if(player.gethasRecuit()){
            counter++;
            settleMission10Description.setForeground(Color.GREEN);
        }else{
            settleMission10Description.setForeground(Color.RED);
        }
        settleMission10Description.setFont(font);
        reportPanel.add(settleMission10Description, "span");

        JLabel settleMission11 = new JLabel("Mission 11:");
        settleMission11.setFont(font);
        reportPanel.add(settleMission11, SPAN_SPLIT_2);

        JLabel settleMission11Description = new JLabel("Buy goods in Europe.");
        if(player.gethasbuyGoods()){
            counter++;
            settleMission11Description.setForeground(Color.GREEN);
        }else{
            settleMission11Description.setForeground(Color.RED);
        }
        settleMission11Description.setFont(font);
        reportPanel.add(settleMission11Description, "span");

        FreeColProgressBar progressBar
                = new FreeColProgressBar(freeColClient, null, MINMISSIONS,
                MAXMISSIONS, counter,
                STEP);
        reportPanel.add(progressBar, "span");
    }
}

