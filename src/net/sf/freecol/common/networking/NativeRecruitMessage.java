package net.sf.freecol.common.networking;

import net.sf.freecol.common.io.FreeColXMLReader;
import net.sf.freecol.common.model.*;
import net.sf.freecol.common.model.NativeRecruit.NativeRecruitAction;
import net.sf.freecol.common.model.NativeRecruit;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.ai.AIPlayer;
import net.sf.freecol.common.model.Game;
import net.sf.freecol.common.model.NativeRecruit;
import net.sf.freecol.common.model.NativeTrade;
import net.sf.freecol.common.model.Unit;

import javax.xml.stream.XMLStreamException;

import net.sf.freecol.common.model.NativeRecruit.NativeRecruitAction;
import net.sf.freecol.server.FreeColServer;
import net.sf.freecol.server.ai.AIPlayer;
import javax.xml.stream.XMLStreamException;

/**
 * The message sent when trading with a native unit.
 *
 * will happen when a unit is trying to recruit a native unit.
 * it will be like a normal trade, but with a unit instead of a settlement.
 * and the unit will be recruited instead of the settlement being added to the player.
 * the unit will be added to the player.
 * the player will be able to move the unit where he wants.
 *
 * the unit will be removed from the native settlement.
 */

public class NativeRecruitMessage extends ObjectMessage {
    private static final String TAG = "nativeRecruit";
    private static final String ACTION_TAG = "action";

    /**
     * Create a new {@code NativeRecruitMessage} request with the
     * supplied unit and nativeUnit.
     *
     * @param unit The {@code Unit} performing the trade.
     * @param nativeUnit The {@code nativeUnit} to be recruited.
     *
     */
    public NativeRecruitMessage(Unit unit, Unit nativeUnit) {
        this(NativeRecruitAction.OPEN, new NativeRecruit(unit, nativeUnit));
    }

    /**
     * Create a new {@code NativeRecruitMessage} with the
     * supplied unit and nativeUnit.
     *
     * @param action The {@code NativeRecruitAction}
     *               OPEN, ACCEPT, REJECT, CANCEL
     * @param nt The {@code NativeRecruit}
     *           unit, nativeUnit
     */
    public NativeRecruitMessage(NativeRecruitAction action, NativeRecruit nt) {
        super(TAG, ACTION_TAG, action.toString());

        appendChild(nt);
    }

    /**
     * Create a new {@code NativeRecruitMessage} with the
     * supplied unit and nativeUnit.
     *
     * @param action The {@code NativeRecruitAction}
     *               OPEN, ACCEPT, REJECT, CANCEL
     * @param nt The {@code NativeRecruit}
     *           unit, nativeUnit
     */
    public NativeRecruitMessage(Constants.NativeRecruitInteractionAction action, NativeRecruit nt) {
        super(TAG, ACTION_TAG, action.toString());

        appendChild(nt);
    }

    /**
     * Create a new {@code NativeRecruitMessage} from a stream.
     *
     * @param game The {@code Game} this message belongs to.
     * @param xr The {@code FreeColXMLReader} to read from.
     * @exception XMLStreamException if there is a problem reading the stream.
     */
    public NativeRecruitMessage(Game game, FreeColXMLReader xr)
            throws XMLStreamException {
        super(TAG, xr, ACTION_TAG);

        NativeRecruit nt = null;
        FreeColXMLReader.ReadScope rs
                = xr.replaceScope(FreeColXMLReader.ReadScope.NOINTERN);
        try {
            while (xr.moreTags()) {
                String tag = xr.getLocalName();
                if (NativeTrade.TAG.equals(tag)) {
                    if (nt == null) {
                        nt = xr.readFreeColObject(game, NativeRecruit.class);
                    } else {
                        expected(TAG, tag);
                    }
                } else {
                    expected(NativeTrade.TAG, tag);
                }
                xr.expectTag(tag);
            }
            xr.expectTag(TAG);
        } finally {
            xr.replaceScope(rs);
        }
        appendChild(nt);
    }

    private NativeRecruit.NativeRecruitAction getAction() {
        return getEnumAttribute(ACTION_TAG, NativeRecruitAction.class,
                (NativeRecruitAction)null);
    }

    private NativeRecruit getNativeRecruit() {
        return getChild(0, NativeRecruit.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MessagePriority getPriority() {
        return Message.MessagePriority.LATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void aiHandler(FreeColServer freeColServer, AIPlayer aiPlayer) {
        final NativeRecruit nt = getNativeRecruit();
        final NativeRecruitAction action = getAction();

        aiPlayer.nativeRecruitHandler(action, nt);
    }

}
