package net.sf.freecol.common.model;

import java.util.ArrayList;
import java.util.List;

import static net.sf.freecol.common.model.NativeTrade.getNativeTradeKey;
import static net.sf.freecol.common.util.CollectionUtils.removeInPlace;

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

    /**
     * Get the transaction count.
     *
     * @return The transaction count.
     */
    public int getCount() {
        return this.count;
    }

    /**
     * Set the transaction count.
     *
     * @param count The new transaction count.
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     * Is this transaction complete?
     *
     * @return True if the transaction is over.
     */
    public boolean getDone() {
        return this.count < 0
                || (!canGift());
    }

    /**
     * Set this transaction as complete.
     */
    public void setDone() {
        this.count = -1;
    }

    /**
     * Get the list of items the unit is able to offer the unit.
     * to recruit or to gift to the native unit.
     * (the goods will be deposited on one of the native's settlements)
     *
     * @return The list of {@code NativeTradeItem}s.
     *
     * @see NativeTradeItem
     */
    public List<NativeTradeItem> getUnitToNativeSettlement() {
        return this.unitToNativeSettlement;
    }

    /**
     * get the list of items the native is carrying.
     * (the goods will be deposited on one of the native's settlements)
     *
     */
    public List<NativeTradeItem> getNativeToNativeSettlement() {
        return this.nativeToNativeSettlement;
    }

    /**
     * Add an item to the unit list of items.
     *
     * @param nti The {@code NativeTradeItem} to add.
     */
    public void addToUnit(NativeTradeItem nti) {
        this.unitToNativeSettlement.add(nti);
    }

    /**
     * Remove an item from the unit list of items.
     *
     * @param nti The {@code NativeTradeItem} to remove.
     */
    public void removeFromUnit(NativeTradeItem nti) {
        removeInPlace(this.unitToNativeSettlement, nti.goodsMatcher());
    }

    /**
     * remove all native's goods from the native's list of items.
     * (the goods will be deposited on one of the native's settlements)
     *
     */
    public void removeAllFromNative() {
        this.nativeToNativeSettlement.clear();
    }


    /**
     * Raw initialization of the unit.
     * does not do pricing.
     */
    public void initialize() {
        final Player unitPlayer = this.unit.getOwner();

        final Player nativePlayer = this.nativeUnit.getOwner();

        final Game game = this.unit.getGame();

        for (Goods g : this.unit.getGoodsList()) {
            this.unitToNativeSettlement.add(new NativeTradeItem(game,
                    unitPlayer, nativePlayer, g));
        }
        for (Goods g : this.nativeUnit.getGoodsList()) {
            this.nativeToNativeSettlement.add(new NativeTradeItem(game,
                    nativePlayer, nativePlayer, g));
        }
    }

    /**
     * Merge another compatible native trade into this one.
     *
     * @param nt The {@code NativeTrade} to merge.
     */
    /** TODO **/


    /**
     * Choose the next available upward haggling price.
     *
     * @param price The initial price.
     * @return The new upward haggled price.
     */
    public static int haggleUp(int price) {
        return (price * 11) / 10;
    }

    /**
     * Choose the next available downward haggling price.
     *
     * @param price The initial price.
     * @return The new downward haggled price.
     */
    public static int haggleDown(int price) {
        return (price * 9) / 10;
    }


    // Override FreeColGameObject

    /**
     * {@inheritDoc}
     */
    public boolean isInternable() {
        return false;
    }

    // Override FreeColGameObject

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends FreeColObject> boolean copyIn(T other) {
        NativeTrade o = copyInCast(other, NativeTrade.class);
        if (o == null || !super.copyIn(o)) return false;
        final Game game = getGame();
        this.unit = game.updateRef(o.getUnit());
        this.count = o.getCount();
        this.buy = o.getBuy();
        this.sell = o.getSell();
        this.gift = o.getGift();
        this.item = game.update(o.getItem(), false);
        this.unitToSettlement = game.update(o.getUnitToSettlement(), false);
        this.settlementToUnit = game.update(o.getSettlementToUnit(), false);
        return true;
    }

}
