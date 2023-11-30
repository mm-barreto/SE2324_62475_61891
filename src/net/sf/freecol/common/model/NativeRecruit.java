package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.List;

import static net.sf.freecol.common.model.NativeTrade.getNativeTradeKey;

/**
 * A native unit to be traded with europeans.
 * (recruited by europeans)
 *
 */

public class NativeRecruit extends FreeColGameObject {

    private static final StringTemplate abortTrade
            = StringTemplate.template("");

    @Override
    public String getXMLTagName() {
        return null;
    }

    public static enum NativeRecruitAction {
        // Requests from European trader
        OPEN(false, true),         // Start a new trade session
        CLOSE(true, true),         // End an existing session
        RECRUIT(false, true),          // recruit the native unit
        GIFT(false, true),         // Gift goods to the native unit
        // Positive responses from native trader
        ACK_OPEN(false, false),    // Open accepted
        ACK_RECRUIT(false, false),     // recruit accepted
        ACK_GIFT(false, false),    // Gift accepted
        ACK_RECRUIT_HAGGLE(false,false),    // Haggle accepted
        // Negative responses from native trader
        NAK_GOODS(false, false),   // Gift failed due to storage
        NAK_HAGGLE(true, false),   // Trade failed due to too much haggling
        NAK_HOSTILE(true, false),  // Natives are hostile
        NAK_NOSALE(true, false),   // Nothing to trade
        NAK_INVALID(true, false);  // Trade is completely invalid

        /** Does this action close the trade? */
        private final boolean closing;

        /** Should this action originate with a European player? */
        private final boolean fromEuropeans;


        /**
         * Create a new native recruit trade action.
         *
         * @param closing If true this is an action that closes the session.
         * @param fromEuropeans True if a European action.
         */
        NativeRecruitAction(boolean closing, boolean fromEuropeans) {
            this.closing = closing;
            this.fromEuropeans = fromEuropeans;
        }

        /**
         * Is this a closing action?
         *
         * @return True if a closing action.
         */
        public boolean isClosing() {
            return this.closing;
        }

        /**
         * Should this action have come from a European player?
         *
         * @return True if a European action.
         */
        public boolean isEuropean() {
            return this.fromEuropeans;
        }
    };

    /** The unit that is trading. */
    private Unit unit;

    /** The nativeUnit to trade with. */
    private Unit nativeUnit;

    /** How many times this trade has been tried. */
    private int count;

    /** True if no gifts made in this trade. */
    private boolean gift;

    /** the item to be traded is the native unit.
     * but the goods and/or gold will be traded as well, and deposited on the native's current settlement.
     * */
    private IndianSettlement settlement;

    /**
     * The goods on the unit that are being offered to the native unit to be recruited.
     * it will be deposited on the native's current settlement.
     */
    private List<NativeTradeItem> unitToNativeSettlement = new ArrayList<>();

    /**
     * goods that the native is carrying with him, will also be deposited on the native's current settlement.
     */
    private List<NativeTradeItem> nativeToNativeSettlement = new ArrayList<>();

    /**
     * Simple constructor, used in Game.newInstance.
     *
     * @param game The enclosing {@code Game}.
     * @param id The identifier (ignored).
     */
    public NativeRecruit(Game game, String id) {
        super(game, id);
    }

    /**
     * Create a new trade session.
     *
     * @param unit The {@code Unit} that is trading.
     * @param is The {@code IndianSettlement} to trade with.
     */
    public NativeRecruit(Unit unit, Unit nativeUnit) {
        this(unit.getGame(), ""); // Identifier not needed

        this.unit = unit;
        this.nativeUnit = nativeUnit;
        this.count = 0;
        this.gift = true;
    }

    /**
     * Check if the trade participants are at war.
     *
     * @return True if the traders are at war.
     */
    private boolean atWar() {
        return this.nativeUnit.getOwner().atWarWith(this.unit.getOwner());
    }

    /**
     * Get a key for this transaction.
     *
     * @return A suitable key.
     */
    /** TODO **/

    /**
     * Get the unit that is trading.
     *
     * @return The {@code Unit} that started the trade.
     */
    public Unit getUnit() {
        return this.unit;
    }

    /**
     * Get the native unit that is trading.
     *
     * @return The {@code Unit} that started the trade.
     */
    public Unit getNativeUnit() {
        return this.nativeUnit;
    }

    /**
     * Get the native settlement that is receiving the goods.
     *
     * @return The {@code IndianSettlement} that started the trade.
     */
    public IndianSettlement getSettlement() {
        return this.settlement;
    }

    /**
     * Is giving available in this transaction?
     *
     * @return True if no blocking gift has been made.
     */
    public boolean getGift() {
        return this.gift;
    }

    /**
     * Can the unit owner give more items in this session at present?
     *
     * @return True if not blocked, and the unit has gifts to give.
     */
    public boolean canGift() {
        return getGift() && this.unit.hasGoodsCargo();
    }

    /**
     * Set the gift state.
     *
     * @param gift The new gift state.
     */
    public void setGift(boolean gift) {
        this.gift = gift;
    }

    /**
     * Have no trades been performed in this transaction?
     *
     * @return True if no blocking gift has occurred.
     */
    public boolean hasNotTraded() {
        return getGift();
    }

}
