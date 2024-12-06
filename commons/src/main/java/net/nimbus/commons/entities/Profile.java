package net.nimbus.commons.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.nimbus.commons.Commons;
import net.nimbus.commons.database.LongIdentifierEntity;

import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Profile implements LongIdentifierEntity {

    private Long id;

    private String uuid;

    private Long money;

    private String inventory;

    private boolean active;

    public String getName() {
        Optional<NimbusPlayer> optional = Commons.getInstance().getNimbusPlayerService().get(uuid);
        return optional.map(NimbusPlayer::getName).orElse("undefined");
    }

    public boolean hasIsland() {
        if (Commons.getInstance().getIslandService().getFounderOf(this).isPresent()) return true;
        return Commons.getInstance().getNimbusPlayerService().query("SELECT * FROM islands WHERE profile_id = ?", id).isPresent();
    }

    //TODO Leveling
}
