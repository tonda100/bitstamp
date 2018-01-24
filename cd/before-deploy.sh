#!/usr/bin/env bash
if [ "$TRAVIS_BRANCH" = 'dev' ] && [ "$TRAVIS_PULL_REQUEST" == 'false' ]; then
    openssl aes-256-cbc -K $encrypted_e6ab63f51501_key -iv $encrypted_e6ab63f51501_iv -in codesigning.asc.enc -out codesigning.asc -d
    gpg --fast-import cd/signingkey.asc
fi