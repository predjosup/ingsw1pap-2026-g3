#include <stdio.h>
#include <string.h>
#include <openssl/sha.h>

static const unsigned char EXPECTED_LICENSE_HASH[SHA256_DIGEST_LENGTH] = {
    0x8f, 0xcf, 0x71, 0x78, 0x34, 0x0e, 0x4d, 0xe0,
    0xa7, 0x31, 0x28, 0xb8, 0x9b, 0xf4, 0x95, 0xba,
    0x04, 0x68, 0xac, 0xb5, 0x7f, 0x2b, 0x1f, 0x03,
    0x73, 0x26, 0x5c, 0x98, 0x36, 0xce, 0x29, 0x64
};

int main(int argc, char **argv) {
    if (argc < 2) {
        printf("LICENZA_NON_VALIDA: codice mancante\n");
        return 1;
    }

    unsigned char input_hash[SHA256_DIGEST_LENGTH];
    SHA256((const unsigned char *) argv[1], strlen(argv[1]), input_hash);

    if (memcmp(input_hash, EXPECTED_LICENSE_HASH, SHA256_DIGEST_LENGTH) == 0) {
        printf("LICENZA_VALIDA\n");
        return 0;
    }

    printf("LICENZA_NON_VALIDA\n");
    return 1;
}
