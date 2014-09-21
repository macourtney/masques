(ns fixtures.profile)

(def fixture-table-name :profile)

(def records [
  { :id 0
    :alias "test-user"
    :alias-nick nil
    :time-zone nil
    :avatar-file-id nil
    :avatar-nick-file-id nil
    :comments ""
    :destination "pR-QlZfMy3edvvUVMyLBsHvONlskEjWVHk6LPzp3UCEKyx9l6y7AYsGPlUprlfb9QiDQIamxxy6ohsArHvHpdrrFD0fOe9SEWicqnm0VFwY8v6gib-HR5TA~19jcvzqxPKM2v1i4NLVofmbR0b-e~zsdC8~QrY4W8PGeY56lQaSWn9SqPj06EqudaI8VJdkiyTUnvUZY0ReZP5Hn4Bec47QCmL6njtd9UCNkK0jrrmlN0kXBBNH1ICfQa89HAvBE3S7IC2joJXCw1mdr7J9JHqies9DqVEKMFAqC0KHVQvR7MYn47OwIGIcxQm~tKLU~qyMqbdSUsA66JQJCquVjSE~pIU6KxgD-5vz4Dz9tohpI9bQiUkSkyYeydv3pYLegODM~79l6kfszPiBi1Eq7aJhjvvzvV13FO4FjxKVPRJJfnr6vOnJjW2cEauNpriiY-GlOmcWePrro7fN2vceL1M7DlC28icYBZV4YBiwLZ4hr0soCyAS6oiB9P6EBYmNHAAAA"
    :identity "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCT2WphqSPNsWOXWCOCLPnVcNY2kQtHgpzpIr/hO+YXSwgH2OjZtuX7Y/1ILLCZGtQcEKb1q7AEgo1UlCt2khgTVX+fUc3Qe66a7Y2x0CbBR2e4MM5BSNlHHC3TnR0UGiY8qX4YQt/Wey02uxKw5uVpauuuED28S/PXLCUrAYeCAwIDAQAB"
    :identity-algorithm "RSA"
    :private-key "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJPZamGpI82xY5dYI4Is+dVw1jaRC0eCnOkiv+E75hdLCAfY6Nm25ftj/UgssJka1BwQpvWrsASCjVSUK3aSGBNVf59RzdB7rprtjbHQJsFHZ7gwzkFI2UccLdOdHRQaJjypfhhC39Z7LTa7ErDm5Wlq664QPbxL89csJSsBh4IDAgMBAAECgYA7sofv0vmv7jZGP8Jmp35hHmSAN+SUBTsSL4PGkAcB1LvzXzP15JHMBb2ZTOIpj9mhU1/2xlIWIBis0/8Qq0CovdpS+3AD7jhNjbdA3Rv28qsqa0D/hbpniAkA/ezkpNDF8Ag/gbDv20zB5shj/gpjpv4pPY8LOp1YcfoCJLd6oQJBAOuEnxyq0tXKGqtbbcR449+3cwhJZlwA4jgeVx1e5NReKbtlUcB4N1HDUY2MtwjesTXBZJAwtt811f0xjFC4sxsCQQCgtQJbiKdo05LBbJ2Cc5shWwyAdqpLnW1Sku4aubqfkHCRqs2SeqVK/yhDdjMTni6+GKiK8PpDlRd1yDhUf/M5AkA+UGy47QmzvzGnPR2h6kqAms042BLZLPKt3nk2MDFjbzajen9S6XvZilA9n4meMy24B19QN1NrY5cm0sFJalUZAkAec23o7jHaeQx7vhryVvl0Do6F4PZPsZq/ZLvdMIgeJ/5Me7LMKJUdas+0SLdQ5k4xEvcMrLCfEacKWE/kIwJxAkBGGAsrMuqf6DTZHm5mxiBJxuctymY44V1r7UjB1F0jIE4BhLUKe2PNYCKcr84vwA6etdqPF+MyCmelpcVCAb2y"
    :private-key-algorithm "RSA" }
  { :id 1
    :alias "friend"
    :alias-nick nil
    :time-zone nil
    :avatar-file-id nil
    :avatar-nick-file-id nil
    :comments ""
    :destination "pR-QlZfMy3edvvUVMyLBsHvONlskEjWVHk6LPzp3UCEKyx9l6y7AYsGPlUprlfb9QiDQIamxxy6ohsArHvHpdrrFD0fOe9SEWicqnm0VFwY8v6gib-HR5TA~19jcvzqxPKM2v1i4NLVofmbR0b-e~zsdC8~QrY4W8PGeY56lQaSWn9SqPj06EqudaI8VJdkiyTUnvUZY0ReZP5Hn4Bec47QCmL6njtd9UCNkK0jrrmlN0kXBBNH1ICfQa89HAvBE3S7IC2joJXCw1mdr7J9JHqies9DqVEKMFAqC0KHVQvR7MYn47OwIGIcxQm~tKLU~qyMqbdSUsA66JQJCquVjSE~pIU6KxgD-5vz4Dz9tohpI9bQiUkSkyYeydv3pYLegODM~79l6kfszPiBi1Eq7aJhjvvzvV13FO4FjxKVPRJJfnr6vOnJjW2cEauNpriiY-GlOmcWePrro7fN2vceL1M7DlC28icYBZV4YBiwLZ4hr0soCyAS6oiB9P6EBYmNHAAAA"
    :identity "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCBbntriSYcylfNxTLZNB5tVXHbgSLphQHM++tI0E9wUZnJj7Rh29lgKn4F3W5CXBHFJJDqBJLv2Q5o77pDNlu5H5zAz/AQlzxn/TuFlDYkyBkOv0TUZU3Na7KUqMOBZNr+G1c64VQN4XFkIz5Rh8ki/QhTsNXnvwDOnrMl++FQawIDAQAB"
    :identity-algorithm "RSA"
    :private-key nil
    :private-key-algorithm nil}])

(def fixture-map { :table fixture-table-name :records records })