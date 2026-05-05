#include <stdio.h>
#include <string.h>

static void trim_newline(char *text) {
    if (text == NULL) {
        return;
    }
    size_t len = strlen(text);
    while (len > 0 && (text[len - 1] == '\n' || text[len - 1] == '\r')) {
        text[len - 1] = '\0';
        len--;
    }
}

int main(int argc, char **argv) {
    if (argc < 2) {
        printf("LICENZA_NON_VALIDA: codice mancante\n");
        return 1;
    }

    char buffer[256];
    snprintf(buffer, sizeof(buffer), "%s", argv[1]);

    trim_newline(buffer);

    if (strcmp(buffer, "BLACKJACK-2026-VALID") == 0) {
        printf("LICENZA_VALIDA\n");
        return 0;
    }

    printf("LICENZA_NON_VALIDA\n");
    return 1;
}
