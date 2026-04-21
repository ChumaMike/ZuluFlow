# 🛡️ ZuluFlow — Beneficiary Service

A secure, immutable ledger of approved payment recipients. Built to sit in front of a payments engine and enforce that money only ever moves to vetted beneficiaries.

---

## Why

In corporate payments, the most expensive fraud isn't forged invoices — it's **altered beneficiary details**. An attacker compromises one AP clerk, swaps a bank account on a recurring supplier, and siphons legitimate payments for months. ZuluFlow makes the beneficiary list the security boundary.

---

## Design principles

- **Append-only ledger** — beneficiaries are never "updated" in place; each change is a new revision with its own approval chain
- **Two-person integrity** — adding or changing a beneficiary requires a maker and a separate checker
- **Hash-chained history** — each revision carries the hash of the previous revision, so silent DB tampering is detectable
- **Idempotent API** — payment engines can check-then-pay without races

---

## Stack

| Layer | Tech |
| :--- | :--- |
| API | Java 17 · Spring Boot |
| DB | PostgreSQL with JSONB revision records |
| Auth | Spring Security · JWT |
| Tests | JUnit 5 · Testcontainers |

---

## Quick start

```bash
./mvnw spring-boot:run

# In another shell, seed + exercise the API
curl -X POST http://localhost:8080/beneficiaries \
  -H 'Content-Type: application/json' \
  -d '{"name":"Acme Cleaning","account":"1234567890","bank":"FNB"}'
```

See `src/main/resources/application.yml` for configuration.

---

## 🗺️ Roadmap

- [ ] WebAuthn for the approver step (no more password-based sign-off)
- [ ] Event stream to a Kafka topic for downstream payment engines
- [ ] SWIFT BIC validation
- [ ] React admin console for auditors

---

## License

MIT · Built by [Chuma Meyiswa](https://github.com/ChumaMike)
