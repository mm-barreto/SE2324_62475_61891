package net.sf.freecol.common.model;

import net.sf.freecol.common.io.FreeColXMLReader;

import javax.xml.stream.XMLStreamException;

public abstract class Recruitable extends FreeColGameObject {
    /**
     * The player who is to provide this unit.
     */
    private Player source;

    /**
     * The player who is to receive this unit.
     */
    private Player destination;

    /**
     * Flag for validity tests.
     */
    public static final int INVALID_UNIT = Integer.MIN_VALUE;

    /**
     * Creates a new {@code Recruitable} instance.
     *
     * @param game The enclosing {@code Game}.
     * @param id The object identifier.
     * @param source The source {@code Player}.
     * @param destination The destination {@code Player}.
     */
    protected Recruitable(Game game, String id, Player source,
                        Player destination) {
        super(game, id);

        this.source = source;
        this.destination = destination;
    }

    /**
     * Creates a new {@code Recruitable} instance.
     *
     * @param game The enclosing {@code Game}.
     * @param xr The {@code FreeColXMLReader} to read from.
     * @exception XMLStreamException if there is a problem reading the stream.
     */
    protected Recruitable(Game game, FreeColXMLReader xr)
            throws XMLStreamException {
        super(game, "");

        readFromXML(xr);
    }

    /**
     * Get the source player.
     *
     * @return The source {@code Player}.
     */
    public final Player getSource() {
        return this.source;
    }

    /**
     * Set the source player.
     *
     * @param newSource The new source {@code Player}.
     */
    public final void setSource(final Player newSource) {
        this.source = newSource;
    }

    /**
     * Get the destination player.
     *
     * @return The destination {@code Player}.
     */
    public final Player getDestination() {
        return this.destination;
    }

    /**
     * Set the destination player.
     *
     * @param newDestination The new destination {@code Player}.
     */
    public final void setDestination(final Player newDestination) {
        this.destination = newDestination;
    }

    /**
     * Get the other player for this Recruitable
     *
     * @param player The {@code Player} we do not want.
     * @return The {@code Player} we want.
     */
    public final Player getOther(Player player) {
        return (player == this.source) ? this.destination : this.source;
    }

    // The following routines must be supplied/overridden by the subclasses.

    /**
     * Is this recruitable valid?  That is, is the request well formed.
     *
     * @return True if the unit is valid.
     */
    public abstract boolean isValid();

    /**
     * Is this trade item unique?
     * This is true for the StanceTradeItem and the GoldTradeItem,
     * and false for all others.
     *
     * @return True if the item is unique.
     */
    public abstract boolean isUnique();

    /**
     * Get a label for this item.
     *
     * @return A {@code StringTemplate} describing this item.
     */
    public abstract StringTemplate getLabel();

    /**
     * Get the colony to trade.
     *
     * @param game A {@code Game} to look for the colony in.
     * @return The {@code Colony} to trade.
     */
    public Colony getColony(Game game) { return null; }

    /**
     * Get the goods to trade.
     *
     * @return The {@code Goods} to trade.
     */
    public Goods getGoods() { return null; }

    /**
     * Set the goods to trade.
     *
     * @param goods The new {@code Goods} to trade.
     */
    public void setGoods(Goods goods) {}

    /**
     * Get the goods to trade.
     *
     * @return The {@code unit} to trade.
     */
    public Unit getUnit() { return null; }

    /**
     * Set the goods to trade.
     *
     * @param unit The new {@code Goods} to trade.
     */
    public void setUnit(Unit unit) {}

    /**
     * Get the gold to trade.
     *
     * @return The gold to trade.
     */
    public int getGold() { return 0; }

    /**
     * Set the gold to trade.
     *
     * @param gold The new gold to trade.
     */
    public void setGold(int gold) {}

    /**
     * Get the stance to trade.
     *
     * @return The {@code Stance} to trade.
     */
    public Stance getStance() { return null; }

    /**
     * Set the stance to trade.
     *
     * @param stance The new {@code Stance} to trade.
     */
    public void setStance(Stance stance) {}

    /**
     * Evaluate this trade item for a given player.
     *
     * @param player The {@code Player} to evaluate for.
     * @return A value for the player, INVALID_TRADE_ITEM for invalid.
     */
    public abstract int evaluateFor(Player player);


    // Override FreeColGameObject

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInternable() {
        return false;
    }

    @Override
    public String getXMLTagName() {
        return null;
    }
}
