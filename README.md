# Joypad Enhanced — Minecraft 1.7.10

Versão melhorada do JoypadMod com suporte nativo a controles **Bluetooth/USB no macOS**, correções de **XInput no Windows** e uma interface de configuração revisada.

**[Baixar a versão 0.2.0](https://github.com/patodois/MinecraftJoypadSplitscreenMod/releases/tag/v0.2.0)** · **[Build instructions and details](MinecraftJoypadSplitscreenMod/README.md)**

## Instalação

1. Use Minecraft **1.7.10 + Forge + Java 8**.
2. Feche o jogo, retire o JoypadMod antigo de `mods` e coloque o JAR da nova versão.
3. Em **Opções → Controles**, selecione o dispositivo e abra **Testar controle**.

No Mac, o controle aparece como `macOS - Xbox Wireless Controller` ou o nome correspondente. O leitor nativo requer **macOS 11.3+**, com binário para Apple Silicon e Intel. No Windows, usa XInput. Não é preciso instalar Controlify, Controllable, YACL ou DLLs separadas.

A detecção e todos os botões/analógicos/gatilhos foram confirmados no teste do Xbox Wireless Controller Bluetooth em um Mac Apple Silicon, dentro do Tekxit com Forge 10.13.4.1558. A versão também passou em 101 verificações de entrada simulada. Windows e Mac Intel ainda precisam de testes físicos.

Este é um **fork experimental**, baseado na branch 1.7.10 do [projeto original](https://github.com/ljsimin/MinecraftJoypadSplitscreenMod), de Ljubomir Simin, Andrew Hickey e colaboradores. A release deste fork não é uma release oficial do projeto original.
