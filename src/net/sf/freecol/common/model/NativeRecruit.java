package net.sf.freecol.common.model;

import net.sf.freecol.common.io.FreeColXMLReader;
import net.sf.freecol.common.io.FreeColXMLWriter;

import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import static net.sf.freecol.common.model.NativeTrade.getNativeTradeKey;
import static net.sf.freecol.common.util.CollectionUtils.any;
import static net.sf.freecol.common.util.CollectionUtils.removeInPlace;

/**
 * A native unit to be traded with europeans.
 * (recruited by europeans)
 *
 */

public class NativeRecruit extends FreeColGameObject {

    private static final Logger logger = Logger.getLogger(NativeRecruit.class.getName());

    public static final String TAG = "nativeRecruit";

    private static final StringTemplate abortRecruit
            = StringTemplate.template("");


    @Override
    public String getXMLTagName() {
        return TAG;
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

    /** True if the unit is being recruited. */
    private boolean recruit;

    /** An item under consideration for a transaction. */
    private NativeTradeItem item;


    /**
     * The goods on the unit that are being offered to the native unit to be recruited.
     * it will be deposited on the native's current settlement.
     */
    private List<NativeTradeItem> unitToNativeSettlement = new ArrayList<>();

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
     * @param nativeUnit The {@code IndianSettlement} to trade with.
     */
    public NativeRecruit(Unit unit, Unit nativeUnit) {
        this(unit.getGame(), ""); // Identifier not needed

        this.unit = unit;
        this.nativeUnit = nativeUnit;
        this.count = 0;
        this.recruit = this.gift = true;
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
    public String getKey() {
        return getRecruitTradeKey(this.unit, this.nativeUnit);
    }

    /**
     * Make a transaction key for a native trade.
     *
     * @param unit The {@code Unit} that is trading.
     *             (the unit that is trying to recruit the native unit)
     * @param nativeUnit The {@code Unit} that is trading.
     *                   (the unit that is being recruited)
     * @return A suitable key.
     */
    public static String getRecruitTradeKey(Unit unit, Unit nativeUnit) {
        return unit.getId() + "-" + nativeUnit.getId();
    }


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
     * is recruiting available in this transaction?
     *
     * @return True if no blocking recruit has been made.
     */
    public boolean getRecruit() {
        return this.recruit;
    }

    /**
     * Can the unit owner recruit the native unit in this session at present?
     *
     * @return True if not blocked, and the unit has goods to give.
     */
    public boolean canRecruit() {
        return getRecruit() && !atWar() && any(getUnitToNativeSettlement(), NativeTradeItem::priceIsValid);
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
     * set the recruit state.
     * @param recruit The new recruit state.
     */
    public void setRecruit(boolean recruit) {
        this.recruit = recruit;
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
                || (!canGift() && !canRecruit());
    }

    /**
     * Set this transaction as complete.
     */
    public void setDone() {
        this.count = -1;
    }

    /**
     * Get the item being traded.
     *
     * @return The current {@code NativeTradeItem}.
     */
    public NativeTradeItem getItem() {
        return this.item;
    }

    /**
     * Set the item being traded.
     *
     * @param nti The new {@code NativeTradeItem}.
     */
    public void setItem(NativeTradeItem nti) {
        this.item = nti;
    }

    public Unit getUnitToRecruit() {
        return this.nativeUnit;
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
     * Is another native recruit compatible with this one?
     *
     * @param nt The {@code NativeRecruit} to check.
     *
     * @return True if compatible.
     */
    public boolean isCompatible(NativeRecruit nt) {
        return this.getKey().equals(nt.getKey());
    }

    /**
     * remove all native's goods from the native's list of items.
     * (the goods will be deposited on one of the native's settlements)
     *
     */
    public void removeAllItemsFromNative() {
        this.nativeUnit.getGoodsList().clear();
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
    }

    /**
     * Merge another compatible native trade into this one.
     *
     * @param nt The {@code NativeTrade} to merge.
     */
    public void merge(final NativeRecruit nt) {
        if (isCompatible(nt) && this.equals(nt)) {
            this.unitToNativeSettlement.clear();
            this.unitToNativeSettlement.addAll(nt.getUnitToNativeSettlement());
            this.item = nt.getItem();
        }
    }

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
        NativeRecruit o = copyInCast(other, NativeRecruit.class);
        if (o == null || !super.copyIn(o)) return false;
        final Game game = getGame();
        this.unit = game.updateRef(o.getUnit());
        this.nativeUnit = game.updateRef(o.getNativeUnit());
        this.count = o.getCount();
        this.recruit = o.getRecruit();
        this.gift = o.getGift();
        this.item = game.update(o.getItem(), false);
        this.unitToNativeSettlement = game.update(o.getUnitToNativeSettlement(), false);
        return true;
    }

    // Serialization
    private static final String RECRUIT_TAG = "recruit";
    private static final String COUNT_TAG = "count";
    private static final String GIFT_TAG = "gift";
    private static final String UNIT_TAG = "unit";
    private static final String NATIVE_UNIT = "nativeUnit";
    private static final String UNIT_TO_NATIVE_SETTLEMENT_TAG = "unitToNativeSettlement";

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeAttributes(FreeColXMLWriter xw) throws XMLStreamException {
        super.writeAttributes(xw);

        xw.writeAttribute(RECRUIT_TAG, this.recruit);

        xw.writeAttribute(COUNT_TAG, this.count);

        xw.writeAttribute(GIFT_TAG, this.gift);

        xw.writeAttribute(NATIVE_UNIT, this.nativeUnit.name + "\nExp:" + this.nativeUnit.experience + "\nExp Type:" + this.nativeUnit.experienceType + "\nRole:" + nativeUnit.role);

        xw.writeAttribute(UNIT_TAG, this.unit);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void writeChildren(FreeColXMLWriter xw) throws XMLStreamException {
        super.writeChildren(xw);

        xw.writeStartElement(UNIT_TO_NATIVE_SETTLEMENT_TAG);

        for (NativeTradeItem nti : this.unitToNativeSettlement) nti.toXML(xw);

        xw.writeEndElement();

        if (this.nativeUnit != null) this.nativeUnit.toXML(xw);
        if (this.item != null) this.item.toXML(xw);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readAttributes(FreeColXMLReader xr) throws XMLStreamException {
        final Game game = getGame();
        super.readAttributes(xr);

        this.recruit = xr.getAttribute(RECRUIT_TAG, false);

        this.count = xr.getAttribute(COUNT_TAG, -1);

        this.gift = xr.getAttribute(GIFT_TAG, false);

        this.nativeUnit = xr.getAttribute(game, NATIVE_UNIT, Unit.class, (Unit) null);

        this.unit = xr.getAttribute(game, UNIT_TAG, Unit.class, (Unit)null);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readChildren(FreeColXMLReader xr) throws XMLStreamException {
        // Clear containers
        this.unitToNativeSettlement.clear();
        this.item = null;
        //this.nativeUnit.setOwner(null);

        /*
         * @Warning
         * this may cause a bug
         */

        super.readChildren(xr);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void readChild(FreeColXMLReader xr) throws XMLStreamException {
        String tag = xr.getLocalName();
        Game game = getGame();

        if (UNIT_TO_NATIVE_SETTLEMENT_TAG.equals(tag)) {
            while (xr.moreTags()) {
                tag = xr.getLocalName();
                if (NativeTradeItem.TAG.equals(tag)) {
                    this.unitToNativeSettlement.add(new NativeTradeItem(game, xr));
                } else {
                    logger.warning("UnitToSettlement-item expected, not: " + tag);
                }
            }

            /**} else if (NATIVE_UNIT.equals(tag)) {
            this.item = new NativeTradeItem(game, xr);
**/
        } else if (NativeTradeItem.TAG.equals(tag)) {
            this.item = new NativeTradeItem(game, xr);

        } else {
            super.readChild(xr);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        NativeTradeItem item = getItem();
        sb.append('[').append(TAG)
                .append(' ').append(getUnit().getId())
                .append(' ').append(getNativeUnit().getId())
                .append(" recruit=").append(getRecruit())
                .append(" gift=").append(getGift())
                .append(" count=").append(getCount())
                .append(" item=").append((item == null) ? "null" : item.toString())
                .append(" unitToNativeSettlement[");
        for (NativeTradeItem nti : this.unitToNativeSettlement) {
            sb.append(' ').append(nti);
        }
        sb.append("] recruiting[");
            sb.append(' ').append(nativeUnit.name).append("\nExp:").append(nativeUnit.experience).append("\nExp Type:").append(nativeUnit.experienceType).append("\nRole:").append(nativeUnit.role);
        return sb.append(" ]]").toString();
    }
}
