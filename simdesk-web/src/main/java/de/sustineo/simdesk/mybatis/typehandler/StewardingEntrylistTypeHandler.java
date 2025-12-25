package de.sustineo.simdesk.mybatis.typehandler;

import de.sustineo.simdesk.entities.stewarding.StewardingEntrylist;

public class StewardingEntrylistTypeHandler extends JsonTypeHandler<StewardingEntrylist> {
    public StewardingEntrylistTypeHandler(Class<StewardingEntrylist> type) {
        super(type);
    }
}
