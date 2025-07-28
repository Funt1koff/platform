package com.bonfire.cache.groups;

import com.bonfire.cache.helpers.ProtoBinaryWrapper;
import com.bonfire.common.Member;
import com.google.protobuf.Parser;

public class MemberWrapper extends ProtoBinaryWrapper<Member> {

    public MemberWrapper() {
    }

    public MemberWrapper(Member value) {
        super(value);
    }

    @Override
    public Parser<Member> parser() {
        return Member.parser();
    }
}
