package com.quantumbanking.modules.pixKey.dict;

import java.util.Optional;

public interface DictClient {
    Optional<DictEntry> lookup(String pixKey);
}