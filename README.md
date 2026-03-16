# NetShield — Analisador de Seguranca de Redes

Aplicativo Android nativo para analise de seguranca de redes Wi-Fi, desenvolvido em Java com arquitetura MVVM.

---

## Sobre o Projeto

O NetShield e um aplicativo Android que permite que qualquer pessoa verifique a seguranca da sua rede Wi-Fi de forma simples e visual. Com ele e possivel identificar quais portas estao abertas, quais dispositivos estao conectados e receber um relatorio completo de vulnerabilidades, sem precisar de conhecimento tecnico avancado.

---

## Funcionalidades

- Scanner de Portas — varre portas TCP de qualquer IP na rede local ou externa
- Scan de Rede — detecta todos os dispositivos conectados ao Wi-Fi via ARP e TCP
- Relatorio de Seguranca — classifica portas em Critico, Atencao e Seguro com descricao de cada protocolo
- Historico — salva todos os scans realizados no banco de dados local
- Suporte a rede externa — varredura de IPs e dominios fora da rede local

---

## Tecnologias Utilizadas

| Tecnologia | Uso |
|---|---|
| Java | Linguagem principal |
| Android SDK | Plataforma |
| MVVM + LiveData | Arquitetura |
| Room + SQLite | Persistencia de dados |
| ExecutorService | Programacao concorrente |
| WifiManager / DhcpInfo | Informacoes de rede |
| ARP (/proc/net/arp) | Deteccao de dispositivos |
| Material Design | Interface |
| CardView | Componentes UI |

---

## Requisitos

- Android 7.0 (API 24) ou superior
- Permissao de acesso a rede Wi-Fi
- Conexao Wi-Fi ativa para scan de rede local

---

## Como Executar

Clone o repositorio:

```bash
git clone https://github.com/SEU_USUARIO/NetShield.git
```

Abra no Android Studio:
- File > Open > selecione a pasta do projeto

Configure o JDK:
- File > Settings > Build > Gradle > JDK 17 ou superior

Execute no dispositivo:

```powershell
$env:JAVA_HOME="C:\Users\SeuUsuario\.jdks\openjdk-25.0.2"
.\gradlew installDebug
```

---

## Banco de Dados

O app utiliza Room (SQLite) com as seguintes tabelas:

- tb_scan — historico de scans realizados
- tb_porta — portas encontradas em cada scan

---

## Como Funciona o Scanner

1. Usuario informa IP e intervalo de portas
2. ExecutorService cria pool de threads
3. Cada thread testa uma porta via socket TCP
4. Portas abertas sao classificadas por nivel de risco
5. Resultado e salvo no banco e exibido no relatorio

---

## Classificacao de Risco

| Nivel | Descricao | Exemplo de Portas |
|-------|-----------|-------------------|
| Critico | Fechar imediatamente | 23 (Telnet), 3389 (RDP) |
| Atencao | Verificar necessidade | 21 (FTP), 80 (HTTP) |
| Seguro | Baixo risco | 443 (HTTPS), 22 (SSH) |

---


## Desenvolvido por

Projeto desenvolvido como trabalho academico para a disciplina de Desenvolvimento de Aplicativos Android.
