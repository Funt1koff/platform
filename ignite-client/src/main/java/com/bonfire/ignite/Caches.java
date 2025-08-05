package com.bonfire.ignite;

import com.bonfire.cache.*;
import com.bonfire.cache.bots.BotEntityWrapper;
import com.bonfire.cache.bots.BotUpdatesState;
import com.bonfire.cache.groups.GroupEntityWrapper;
import com.bonfire.cache.groups.GroupMemberKeyV2;
import com.bonfire.cache.groups.GroupMemberWrapper;
import com.bonfire.cache.groups.GroupMembersWrapper;
import com.bonfire.cache.helpers.FileUploadBinaryWrapper;
import com.bonfire.cache.helpers.ProfilePhotosBinaryWrapper;
import com.bonfire.cache.helpers.UserEntryBinaryWrapper;
import org.apache.ignite.client.ClientCache;
import org.apache.ignite.client.IgniteClient;

import java.util.UUID;

public class Caches {

    public static ClientCache<DeduplicationEntity.KeyV2, DeduplicationEntity> deduplicationCache(IgniteClient ignite) {
        return ignite.cache(CacheNames.DEDUPLICATION);
    }

    public static ClientCache<String, ShortnameEntity> shortnamesCache(IgniteClient ignite) {
        return ignite.cache(CacheNames.SHORTNAMES);
    }

    public static ClientCache<String, PhoneNumberEntity> phoneNumbersCache(IgniteClient ignite) {
        return ignite.cache(CacheNames.USER_PHONE_NUMBER);
    }

    public static ClientCache<Long, UserEntryBinaryWrapper> userCache(IgniteClient ignite) {
        return ignite.cache(CacheNames.USERS);
    }

    public static ClientCache<Long, BotEntityWrapper> botsCache(IgniteClient ignite) {
        return ignite.cache(CacheNames.BOTS);
    }

    public static ClientCache<Long, long[]> userBotsCache(IgniteClient ignite) {
        return ignite.cache(CacheNames.USER_BOTS);
    }

    public static ClientCache<UUID, Long> botTokensCache(IgniteClient ignite) {
        return ignite.cache(CacheNames.BOT_TOKENS);
    }

    public static ClientCache<Long, GroupEntityWrapper> groupsCache(IgniteClient ignite) {
        return ignite.cache(CacheNames.GROUPS);
    }

    public static ClientCache<GroupMemberKeyV2, GroupMemberWrapper> groupMemberCache(IgniteClient ignite) {
        return ignite.cache(CacheNames.GROUP_MEMBER);
    }

    public static ClientCache<Long, GroupMembersWrapper> groupMembersCache(IgniteClient ignite) {
        return ignite.getOrCreateCache(CacheNames.GROUP_MEMBERS);
    }

    public static ClientCache<FileUploadKey, FileUploadBinaryWrapper> uploadsCache(IgniteClient ignite) {
        return ignite.getOrCreateCache(CacheNames.FILE_UPLOADS);
    }

    public static ClientCache<Long, ProfilePhotosBinaryWrapper> userProfilePhotosCache(IgniteClient ignite) {
        return ignite.cache(CacheNames.USER_PROFILE_PHOTOS);
    }

    public static ClientCache<Long, BotUpdatesState> botUpdatesStatesCache(IgniteClient ignite) {
        return ignite.getOrCreateCache(CacheNames.BOT_UPDATE_STATES);
    }
}
