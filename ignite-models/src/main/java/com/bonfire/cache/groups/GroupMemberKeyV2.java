package com.bonfire.cache.groups;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import org.apache.ignite.cache.affinity.AffinityKeyMapped;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class GroupMemberKeyV2 {

    @AffinityKeyMapped
    long groupId;

    @Getter
    MemberWrapper memberKey;
}
