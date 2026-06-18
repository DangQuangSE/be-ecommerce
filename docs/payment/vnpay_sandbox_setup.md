# VNPay Sandbox Setup

## Payment method

VNPay orders use `paymentMethod: BANK_TRANSFER` (không có enum `VNPAY` riêng).

## Environment variables (`.env`)

```env
VNPAY_TMN_CODE=your_sandbox_tmn_code
VNPAY_HASH_SECRET=your_sandbox_hash_secret
VNPAY_PAY_URL=https://sandbox.vnpayment.vn/paymentv2/vpcpay.html
VNPAY_RETURN_URL=sportpro://payment-result
VNPAY_IPN_URL=https://your-public-host/api/v1/payments/vnpay/ipn
```

## IPN URL

VNPay gọi `GET /api/v1/payments/vnpay/ipn` (public, không JWT). URL phải reachable từ internet.

## Mobile return URL

Flutter dùng deep link `sportpro://payment-result`. Nếu sandbox không chấp nhận custom scheme, đặt:

```env
VNPAY_RETURN_URL=https://your-public-host/api/v1/payments/vnpay/bridge-return
```

Endpoint bridge redirect 302 về `sportpro://payment-result?...`.

## API flow

1. `POST /api/v1/orders` — `paymentMethod: BANK_TRANSFER` (stock/cart deferred)
2. `POST /api/v1/payments/vnpay/create` — `{ "orderId": 1 }`
3. App mở `paymentUrl` trong WebView
4. VNPay → IPN → `paymentCompleted=true`, fulfill stock/cart, order vẫn `PENDING` (admin xác nhận sau)
5. App → `GET /api/v1/payments/vnpay/verify/{orderId}` → `paymentStatus=SUCCESS` khi `paymentCompleted=true`

## Android emulator

Flutter `BASE_URL` mặc định: `http://10.0.2.2:8080`
