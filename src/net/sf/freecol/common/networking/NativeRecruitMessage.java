package net.sf.freecol.common.networking;

import net.sf.freecol.common.model.NativeRecruit;
import net.sf.freecol.common.model.Unit;
import net.sf.freecol.common.model.NativeRecruit.NativeRecruitAction;

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
    private static final String TAG = "nativeUnitTrade";

    /**
     * Create a new {@code NativeTradeMessage} request with the
     * supplied unit and nativeUnit.
     *
     * @param unit The {@code Unit} performing the trade.
     * @param nativeUnit The {@code nativeUnit} to be recruited.
     *
     */
    public NativeRecruitMessage(Unit unit, Unit nativeUnit) {
        this(NativeRecruitAction.OPEN, new NativeRecruit(unit, nativeUnit));
    }


}
