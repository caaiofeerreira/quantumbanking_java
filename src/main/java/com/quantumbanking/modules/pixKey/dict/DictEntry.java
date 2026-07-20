package com.quantumbanking.modules.pixKey.dict;

import com.quantumbanking.modules.pixKey.domain.PixKeyType;

public record DictEntry(String pixKey,
                        PixKeyType keyType,
                        String ownerDocument,
                        String ownerName,
                        String bankCompe) {

}