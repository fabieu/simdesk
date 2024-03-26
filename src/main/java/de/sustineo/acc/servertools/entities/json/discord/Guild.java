package de.sustineo.acc.servertools.entities.json.discord;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class Guild {
    private String id;
    private String name;
    private String icon;
    private Boolean owner;
    private String permissions;
    private List<String> features;
    @JsonProperty("approximate_member_count")
    private Integer approximateMemberCount;
    @JsonProperty("approximate_presence_count")
    private Integer approximatePresenceCount;
}
