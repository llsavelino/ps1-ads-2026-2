# ps1-ads-2026-2 Repository of the Script Programming I (Back-End) course

## Autenticação e recuperação de senha

- `POST /users/login` recebe `usernameOrEmail` e `password`; em caso de sucesso, `data.token` contém o JWT.
- `POST /users/forgot-password` recebe `email`. A resposta é genérica para não revelar se o e-mail está cadastrado. Para contas existentes, envia um link de redefinição.
- `POST /users/reset-password` recebe `token` e `newPassword` (8 a 72 caracteres). O token expira em 15 minutos e só pode ser usado uma vez.

Configure o envio de e-mail pelas variáveis de ambiente `SMTP_HOST`, `SMTP_PORT`, `SMTP_USERNAME`, `SMTP_PASSWORD` e `SMTP_FROM`. `SMTP_AUTH` e `SMTP_STARTTLS` são opcionais (padrão `true`). Configure `PASSWORD_RESET_URL` com a URL da tela de redefinição do cliente; essa tela deve enviar o token e a nova senha para `POST /users/reset-password`.

## Rate limiting

A API limita cada endereço IP a 100 requisições a cada 60 segundos, usando uma janela deslizante mantida em memória por instância. Ao exceder o limite, retorna HTTP `429 Too Many Requests`, o contrato padrão de erro e o cabeçalho `Retry-After`. O limite e a janela podem ser configurados pelas variáveis `RATE_LIMIT_MAX_REQUESTS` e `RATE_LIMIT_WINDOW_SECONDS`.