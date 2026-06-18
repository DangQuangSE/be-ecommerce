# TÀI LIỆU MÔ TẢ GIAO DIỆN VÀ QUY TRÌNH HOẠT ĐỘNG CỦA ỨNG DỤNG (CHI TIẾT MÀN HÌNH)

Tài liệu này cung cấp mô tả chi tiết về cách hoạt động, giao diện hiển thị, thông tin cần nhập và kết quả phản hồi của từng màn hình trên ứng dụng di động dành cho khách hàng. Tài liệu được viết bằng ngôn ngữ đơn giản, trực quan giúp khách hàng và đối tác dễ dàng theo dõi nghiệp vụ của ứng dụng.

---

## 2.1. Màn hình Đăng nhập (Login Screen)

### 2.1.1. Mục đích
Cho phép người dùng đăng nhập vào tài khoản cá nhân của mình để thực hiện mua sắm, quản lý giỏ hàng, xem thông tin giao hàng và thiết kế in ấn áo theo sở thích.

### 2.1.2. Quy trình hoạt động của màn hình
1. **Kiểm tra trạng thái:** Khi mở ứng dụng, nếu chưa đăng nhập hoặc phiên làm việc trước đó đã hết hạn, hệ thống sẽ tự động đưa người dùng đến màn hình Đăng nhập.
2. **Nhập thông tin:** Người dùng nhập địa chỉ Email và Mật khẩu cá nhân vào hai ô tương ứng, sau đó nhấn nút "Đăng nhập".
3. **Kiểm tra thông tin tại chỗ:** Trước khi gửi thông tin đi, ứng dụng sẽ kiểm tra nhanh:
   - Các ô nhập thông tin có bị bỏ trống hay không.
   - Định dạng Email có hợp lệ hay không (phải có ký tự "@" và tên miền hợp lệ).
   - Độ dài mật khẩu có đạt yêu cầu tối thiểu hay không (ít nhất là 6 ký tự).
4. **Xác thực tài khoản:** Hệ thống gửi thông tin đăng nhập lên máy chủ để đối chiếu dữ liệu. Nhằm đảm bảo an toàn tuyệt đối cho tài khoản của khách hàng, hệ thống sẽ tự động khóa tạm thời nếu phát hiện hành vi nhập sai mật khẩu liên tục nhiều lần (tối đa 5 lần mỗi phút) trong thời gian ngắn.
5. **Đăng nhập thành công:** Khách hàng được đăng nhập vào hệ thống, ứng dụng lưu trạng thái phiên làm việc an toàn và chuyển người dùng đến giao diện mua sắm (hoặc giao diện quản lý dành riêng cho người quản trị).
6. **Đăng nhập thất bại:** Nếu thông tin đăng nhập bị sai, hệ thống sẽ hiển thị dòng chữ thông báo lỗi màu đỏ rõ ràng (ví dụ: "Email hoặc mật khẩu không chính xác") để khách hàng biết và nhập lại.

### 2.1.3. Thông tin đầu vào (Dữ liệu khách hàng nhập)
* **Email:** Địa chỉ thư điện tử đã đăng ký tài khoản (Bắt buộc).
* **Mật khẩu:** Mật khẩu đăng nhập của tài khoản (Bắt buộc, tối thiểu 6 ký tự).

### 2.1.4. Kết quả đầu ra (Phản hồi của ứng dụng)
* **Thành công:** Người dùng được chuyển đến Trang chủ mua sắm của ứng dụng (hoặc Trang quản trị tùy loại tài khoản).
* **Thất bại:** Màn hình giữ nguyên thông tin đã nhập và hiển thị thông báo lỗi chi tiết để khách hàng chỉnh sửa.

---

## 2.1a. Màn hình Đăng ký (Register Screen)

### 2.1a.1. Mục đích
Cho phép khách hàng mới đăng ký tài khoản bằng địa chỉ Email cá nhân để bắt đầu mua sắm và lưu giữ lịch sử thiết kế in ấn sản phẩm.

### 2.1a.2. Quy trình hoạt động của màn hình
1. **Chuyển đổi giao diện:** Khách hàng có thể nhấn vào tab "Đăng ký" ngay trên màn hình Đăng nhập để chuyển sang giao diện Đăng ký.
2. **Nhập thông tin:** Khách hàng nhập địa chỉ Email và đặt Mật khẩu mới mong muốn.
3. **Kiểm tra thông tin tại chỗ:** Ứng dụng tự động kiểm tra tính hợp lệ trước khi gửi yêu cầu:
   - Địa chỉ Email không được để trống và phải nhập đúng cấu trúc thư điện tử thông thường.
   - Mật khẩu đặt mới không được để trống và phải có độ dài ít nhất từ 6 ký tự trở lên.
4. **Yêu cầu mã xác thực OTP:** Khi khách hàng nhấn nút "Đăng ký", ứng dụng sẽ gửi yêu cầu tạo tài khoản lên máy chủ:
   - Hệ thống tự động kiểm tra xem Email này đã được đăng ký trước đó chưa. Nếu đã có tài khoản, hệ thống sẽ hiện thông báo nhắc nhở "Email này đã được sử dụng".
   - Nếu Email chưa từng đăng ký, máy chủ sẽ tự động tạo một mã số xác thực gồm 6 chữ số (mã OTP) và gửi trực tiếp vào hòm thư Email của khách hàng.
   - Để ngăn chặn việc gửi thư rác liên tục, hệ thống giới hạn tối đa chỉ được yêu cầu gửi mã OTP 3 lần mỗi phút.
5. **Chuyển hướng xác minh:** Khi mã OTP được gửi đi thành công, ứng dụng sẽ tự động chuyển khách hàng sang giao diện **Xác minh email (Màn hình OTP)** để tiếp tục.

### 2.1a.3. Thông tin đầu vào (Dữ liệu khách hàng nhập)
* **Email:** Địa chỉ hòm thư muốn đăng ký tài khoản (Bắt buộc).
* **Mật khẩu:** Mật khẩu mong muốn thiết lập (Bắt buộc, tối thiểu 6 ký tự).

### 2.1a.4. Kết quả đầu ra (Phản hồi của ứng dụng)
* **Thành công:** Tự động chuyển hướng khách hàng sang Màn hình Xác thực OTP kèm theo thông tin Email/Mật khẩu tạm lưu.
* **Thất bại:** Giữ nguyên giao diện và hiển thị thông báo lỗi chi tiết (Email đã tồn tại, lỗi định dạng...) để khách hàng điều chỉnh.

---

## 2.1b. Màn hình Xác thực OTP (OTP Verification Screen)

### 2.1b.1. Mục đích
Xác minh khách hàng là chủ sở hữu thực sự của hòm thư Email đã nhập thông qua mã số OTP gồm 6 chữ số để hoàn tất đăng ký tài khoản an toàn.

### 2.1b.2. Quy trình hoạt động của màn hình
1. **Hiển thị thông tin:** Màn hình hiển thị dòng thông báo nhắc nhở nhập mã số và hiển thị Email đã được ẩn bớt ký tự ở giữa (ví dụ: `v***@example.com`) để đảm bảo an toàn thông tin.
2. **Thời gian chờ gửi lại (Cooldown):** Một bộ đếm ngược 60 giây được tự động kích hoạt. Nút "Gửi lại mã OTP" sẽ bị khóa tạm thời trong thời gian đếm ngược này để tránh gửi yêu cầu liên tục. Sau 60 giây, nút này sẽ sáng lên cho phép khách hàng yêu cầu gửi lại mã OTP mới nếu chưa nhận được thư.
3. **Nhập mã xác thực:** Khách hàng nhập 6 chữ số nhận được từ Email vào ô nhập liệu OTP.
4. **Xác thực và tạo tài khoản:** Sau khi nhập đủ 6 số, hệ thống sẽ tự động gửi mã OTP lên máy chủ để đối chiếu:
   - **Xác thực OTP chính xác:** Máy chủ ghi nhận xác minh thành công. Ngay lập tức, hệ thống sẽ thực hiện bước tiếp theo là gửi thông tin đăng ký chính thức để mã hóa mật khẩu, tạo tài khoản mới trong cơ sở dữ liệu và đánh dấu hoàn thành đăng ký.
   - **Mã OTP sai hoặc hết hạn:** Máy chủ sẽ báo lỗi. Nếu khách hàng nhập sai liên tục vượt quá giới hạn an toàn (tối đa 3 lần), mã OTP đó sẽ bị khóa và đánh dấu đã sử dụng (khách hàng bắt buộc phải yêu cầu gửi mã mới).
5. **Hoàn tất đăng ký:** Khi tài khoản được tạo thành công, ứng dụng sẽ tự động đóng giao diện xác thực, hiển thị thông báo nổi "Đăng ký thành công! Vui lòng đăng nhập" và đưa khách hàng quay lại giao diện **Màn hình Đăng nhập** để đăng nhập bằng tài khoản mới.

### 2.1b.3. Thông tin đầu vào (Dữ liệu khách hàng nhập)
* **Mã OTP:** Mã số xác thực gồm 6 chữ số nhận từ Email (Bắt buộc).
* **Hành động:** Chạm nút "Xác nhận" hoặc nút "Gửi lại mã OTP" khi hết thời gian chờ đếm ngược.

### 2.1b.4. Kết quả đầu ra (Phản hồi của ứng dụng)
* **Thành công:** Đăng ký tài khoản thành công, tự động quay về Màn hình Đăng nhập kèm thông báo chúc mừng.
* **Thất bại:** Hiển thị thông báo lỗi chi tiết bên dưới ô nhập OTP (Mã sai, mã hết hạn, hoặc mã đã bị khóa do nhập sai quá nhiều lần).

---

## 2.2. Màn hình Danh sách Sản phẩm (Product List Screen)

### 2.2.1. Mục đích
Hiển thị toàn bộ các sản phẩm thể thao (giày dép, quần áo, phụ kiện) đang được bày bán của cửa hàng, hỗ trợ lọc và tìm kiếm giúp khách hàng nhanh chóng chọn lựa được sản phẩm ưng ý.

### 2.2.2. Quy trình hoạt động của màn hình
1. **Tải danh sách sản phẩm:** Khi khách hàng truy cập màn hình này, ứng dụng sẽ tự động tải danh sách các sản phẩm thể thao mới nhất từ cửa hàng và hiển thị lên màn hình.
2. **Giao diện hiển thị:** Các sản phẩm được sắp xếp gọn gàng dưới dạng lưới 2 cột đẹp mắt. Mỗi ô sản phẩm bao gồm: hình ảnh rõ nét, tên sản phẩm, giá bán chính thức và trạng thái còn hàng hay hết hàng.
3. **Lọc nhanh theo nhóm sản phẩm:** Ở phía trên cùng của màn hình có các thanh chọn danh mục của Sport Pro bao gồm: "Tất cả", "Giày chạy bộ", "Nam", "Size 42" và "Quần áo". Khách hàng chỉ cần chạm vào nhóm mình muốn xem, danh sách sản phẩm bên dưới sẽ tự động thay đổi tương ứng.
4. **Sắp xếp theo giá tiền:** Khi khách hàng nhấn vào nút "Bộ lọc", một bảng tùy chọn nhỏ sẽ hiện lên ở dưới cùng màn hình cho phép sắp xếp sản phẩm:
   - Sắp xếp giá từ thấp đến cao (giá tăng dần).
   - Sắp xếp giá từ cao đến thấp (giá giảm dần).
   - Không sắp xếp (trở về thứ tự mặc định ban đầu).
5. **Xem chi tiết sản phẩm:** Khách hàng nhấn chọn vào bất kỳ sản phẩm nào để chuyển sang màn hình thông tin chi tiết của sản phẩm đó.

### 2.2.3. Thông tin đầu vào (Dữ liệu khách hàng chọn)
* **Chọn danh mục:** Chạm chọn nhóm sản phẩm muốn xem trên thanh trượt ngang ("Tất cả", "Giày chạy bộ", "Nam", "Size 42", "Quần áo").
* **Chọn sắp xếp:** Lựa chọn cách sắp xếp giá tiền (Không sắp xếp, Giá tăng dần hoặc Giá giảm dần).
* **Chọn sản phẩm:** Chạm vào hình ảnh hoặc tên sản phẩm để xem chi tiết.

### 2.2.4. Kết quả đầu ra (Phản hồi của ứng dụng)
* **Tải trang thành công:** Giao diện hiển thị danh sách sản phẩm mượt mà theo đúng bộ lọc và thứ tự giá khách hàng mong muốn.
* **Không tìm thấy kết quả:** Hiển thị thông báo "Không tìm thấy sản phẩm" nếu bộ lọc khách hàng chọn hiện tại không có sản phẩm nào phù hợp.
* **Lỗi đường truyền:** Hiển thị thông báo "Đã xảy ra lỗi khi tải sản phẩm" kèm nút bấm "Thử lại" để khách hàng tải lại trang mà không cần đóng ứng dụng.

---

## 2.3. Màn hình Chi tiết Sản phẩm (Product Detail Screen)

### 2.3.1. Mục đích
Cung cấp đầy đủ thông tin mô tả chi tiết của một sản phẩm (hình ảnh thực tế, mô tả chất liệu, bảng size, đánh giá của người mua trước) và cho phép khách hàng chọn màu sắc, kích cỡ để tiến hành thêm vào giỏ hàng.

### 2.3.2. Quy trình hoạt động của màn hình
1. **Hiển thị thông tin:** Khách hàng xem được toàn bộ thông tin về sản phẩm bao gồm: album ảnh dạng vuốt ngang, tên sản phẩm, giá bán hiển thị rõ ràng, phần mô tả chi tiết chất liệu và các nhận xét đánh giá của khách hàng đã mua trước đó.
2. **Chọn phiên bản sản phẩm:** Khách hàng lựa chọn các thông số theo nhu cầu:
   - **Chọn màu sắc:** Chạm vào các vòng tròn màu sắc có sẵn của sản phẩm.
   - **Chọn kích cỡ (Size):** Chạm chọn kích thước tương ứng (ví dụ: Size 40, 41, 42, 43, 44, 45).
3. **Kiểm tra kho hàng tự động:** Hệ thống tự động tính toán số lượng tồn kho theo đúng màu sắc và kích cỡ khách hàng vừa chọn:
   - Nếu còn hàng: Nút "ADD TO CART" hiển thị màu cam sáng rõ và giá tiền được cập nhật chuẩn xác.
   - Nếu hết hàng: Nút đặt hàng sẽ lập tức chuyển sang màu xám và đổi chữ thành "HẾT HÀNG", đồng thời không cho phép người dùng bấm nút này.
4. **Thêm vào giỏ hàng:** Khi khách hàng nhấn nút "ADD TO CART", ứng dụng sẽ lưu sản phẩm này vào giỏ hàng cá nhân và hiển thị một thông báo nổi ở dưới đáy màn hình xác nhận đã thêm thành công, đi kèm nút bấm "XEM GIỎ" để người dùng truy cập nhanh.
5. **Yêu thích sản phẩm:** Khách hàng có thể nhấn vào biểu tượng hình trái tim để lưu sản phẩm vào danh sách yêu thích cá nhân.

### 2.3.3. Thông tin đầu vào (Dữ liệu khách hàng chọn)
* **Màu sắc:** Màu sản phẩm muốn chọn mua (Mặc định chọn màu đầu tiên).
* **Kích cỡ:** Cỡ sản phẩm muốn chọn mua (Mặc định chọn cỡ đầu tiên).
* **Hành động:** Nhấn chọn nút "ADD TO CART" hoặc nút "Yêu thích".

### 2.3.4. Kết quả đầu ra (Phản hồi của ứng dụng)
* **Giao diện cập nhật:** Thay đổi giá bán hiển thị và trạng thái còn/hết hàng ngay lập tức khi khách hàng thay đổi màu sắc hoặc kích cỡ.
* **Thông báo thành công:** Hiện bảng thông báo nổi "Đã thêm sản phẩm vào giỏ hàng" kèm đường dẫn "XEM GIỎ" để chuyển nhanh tới trang Giỏ hàng.

---

## 2.4. Màn hình Giỏ hàng (Shopping Cart Screen)

### 2.4.1. Mục đích
Cho phép khách hàng xem lại, điều chỉnh và chọn lọc các sản phẩm muốn mua trước khi tiến hành thanh toán. Màn hình hiển thị tổng số tiền tạm tính theo đúng số lượng và sản phẩm khách hàng đã tích chọn.

### 2.4.2. Quy trình hoạt động của màn hình

1. **Hiển thị danh sách sản phẩm trong giỏ:** Màn hình liệt kê toàn bộ sản phẩm đã được thêm vào giỏ. Mỗi sản phẩm hiển thị đầy đủ: hình ảnh thu nhỏ, phân loại sản phẩm, tên sản phẩm, màu sắc, kích cỡ, đơn giá và số lượng đang đặt.

2. **Tích chọn sản phẩm để thanh toán:** Mỗi sản phẩm trong giỏ đều có một ô tích (checkbox) riêng. Khách hàng có thể:
   - Tích chọn từng sản phẩm muốn thanh toán.
   - Bấm **"CHỌN TẤT CẢ"** ở đầu trang để chọn hoặc bỏ chọn toàn bộ danh sách cùng lúc.
   - Tổng tiền ở cuối trang sẽ tự động thay đổi theo đúng các sản phẩm đang được tích chọn.

3. **Tăng/Giảm số lượng:** Bấm nút **(+)** hoặc **(-)** trực tiếp trên từng sản phẩm để điều chỉnh số lượng mua. Tổng tiền được cập nhật tức thì.

4. **Xóa sản phẩm khỏi giỏ hàng:**
   - Bấm nút **(-)** khi số lượng đang là 1, hoặc bấm nút **"XÓA"** (biểu tượng thùng rác) để yêu cầu xóa sản phẩm.
   - Ứng dụng hiện hộp thoại xác nhận: *"Bạn có chắc chắn muốn xóa [Tên sản phẩm] khỏi giỏ hàng?"*
   - Nếu đồng ý, sản phẩm sẽ được gỡ khỏi danh sách ngay lập tức.

5. **Tự thiết kế hình in áo (Chỉ dành cho sản phẩm quần áo - Apparel):**
   - Với sản phẩm quần áo **chưa có thiết kế in**: Thẻ sản phẩm hiển thị nút **"CUSTOM"** (biểu tượng cọ vẽ). Khi nhấn, khách hàng được chuyển sang màn hình thiết kế chuyên biệt để tự tạo chữ in và màu sắc in theo ý thích.
   - Với sản phẩm quần áo **đã có thiết kế in**: Thẻ sản phẩm mở rộng hiển thị thêm **bảng chi tiết thiết kế** phía dưới gồm: ảnh xem trước thiết kế, nội dung chữ in, màu sắc in và phí in ấn phát sinh. Khách hàng có thể bấm **"CHỈNH SỬA THIẾT KẾ"** để thay đổi hoặc **"XÓA THIẾT KẾ"** nếu không muốn in nữa.

6. **Xem tóm tắt hóa đơn:** Bảng chi phí ở cuối trang hiển thị rõ ràng:
   - **Tạm tính:** Tổng tiền các sản phẩm đang được tích chọn (số lượng sản phẩm được ghi kèm theo).
   - **Giao hàng hỏa tốc:** Miễn phí (0đ).
   - **Tổng cộng:** Số tiền thực tế cần thanh toán.

7. **Tiến hành mua hàng:** Nhấn nút **"TIẾN HÀNH THANH TOÁN"** ở cuối trang để chuyển sang màn hình nhập địa chỉ và chọn phương thức thanh toán.

### 2.4.3. Thông tin đầu vào (Dữ liệu khách hàng chọn)
* **Tích chọn sản phẩm:** Chọn/bỏ chọn từng sản phẩm hoặc chọn tất cả để tính vào tổng tiền thanh toán.
* **Số lượng:** Điều chỉnh số lượng mua bằng nút (+) hoặc (-).
* **Xác nhận xóa:** Đồng ý hoặc từ chối xóa sản phẩm khỏi giỏ hàng.
* **Hành động thiết kế:** Bấm **"CUSTOM"** để thêm thiết kế in mới, hoặc **"CHỈNH SỬA / XÓA THIẾT KẾ"** nếu sản phẩm đã có in ấn.
* **Hành động thanh toán:** Bấm **"TIẾN HÀNH THANH TOÁN"** để chuyển sang bước tiếp theo.

### 2.4.4. Kết quả đầu ra (Phản hồi của ứng dụng)
* **Cập nhật tức thì:** Tổng số tiền và số lượng sản phẩm được tính lại ngay khi khách hàng tích chọn, thay đổi số lượng hoặc xóa sản phẩm.
* **Giỏ hàng trống:** Khi không còn sản phẩm nào, màn hình hiển thị biểu tượng túi mua sắm trống kèm thông điệp *"Giỏ hàng của bạn đang trống"* và nút **"TIẾP TỤC MUA SẮM"** để quay lại xem danh sách sản phẩm.

---

## 2.5. Màn hình Thanh toán (Checkout Screen)

### 2.5.1. Mục đích
Ghi nhận thông tin người nhận hàng, lựa chọn hình thức thanh toán (khi nhận hàng COD hoặc trực tuyến qua VNPay) và gửi đơn đặt hàng lên hệ thống của cửa hàng.

### 2.5.2. Quy trình hoạt động của màn hình
1. **Xem thông tin hóa đơn:** Khách hàng kiểm tra lại danh sách sản phẩm muốn mua cùng tổng số tiền cần trả đã được hệ thống tổng hợp từ giỏ hàng.
2. **Điền thông tin nhận hàng:** Khách hàng điền đầy đủ thông tin của người nhận bao gồm: Họ và tên, Số điện thoại liên hệ và Địa chỉ giao hàng chi tiết.
3. **Chọn cách thanh toán:** Khách hàng lựa chọn một trong các phương thức thanh toán:
   - **Thanh toán khi nhận hàng (COD):** Giao hàng tận nơi, khách hàng kiểm tra sản phẩm rồi trả tiền mặt cho nhân viên giao hàng.
   - **Thanh toán online (VNPay):** Trả tiền trực tuyến bằng ví điện tử hoặc tài khoản ngân hàng thông qua cổng thanh toán VNPay Sandbox.
4. **Nhấn Đặt hàng:** Khách hàng kiểm tra kỹ các thông tin rồi nhấn nút "CONFIRM ORDER" (Xác nhận đặt hàng).
5. **Kiểm tra thông tin hợp lệ:** Hệ thống kiểm tra xem khách hàng có bỏ sót thông tin nhận hàng nào không:
   - Nếu bỏ trống: Hệ thống sẽ chặn việc đặt hàng và hiện thông báo nhắc nhở "Vui lòng điền đầy đủ thông tin giao hàng!".
6. **Xử lý đơn hàng:**
   - **Nếu chọn Thanh toán COD:** Đơn hàng được tạo thành công ngay lập tức. Hệ thống dọn sạch giỏ hàng hiện tại trên máy chủ và chuyển khách hàng sang màn hình thông báo Đặt hàng thành công (`/checkout/success`).
   - **Nếu chọn Thanh toán online VNPay:** Ứng dụng sẽ gửi yêu cầu tạo liên kết giao dịch và tự động mở màn hình thanh toán của ngân hàng VNPay (WebView) trực tiếp trên điện thoại của khách hàng.
7. **Xác thực kết quả thanh toán trực tuyến:** Sau khi khách hàng hoàn tất các thao tác thanh toán trên trang ngân hàng, hệ thống sẽ tự động nhận kết quả trả về qua Deep Link, đóng trình duyệt WebView, gọi dịch vụ của máy chủ để xác thực trạng thái giao dịch thành công. Nếu thành công, ứng dụng dọn sạch giỏ hàng và đưa khách hàng tới màn hình Đặt hàng thành công. Nếu thanh toán thất bại hoặc khách hàng hủy thanh toán, hệ thống sẽ hiện thông báo lỗi để khách hàng thử lại.

### 2.5.3. Thông tin đầu vào (Dữ liệu khách hàng nhập)
* **Họ và tên:** Tên của người nhận hàng (Bắt buộc).
* **Số điện thoại:** Số điện thoại để liên lạc khi giao hàng (Bắt buộc).
* **Địa chỉ giao hàng:** Địa chỉ chi tiết để giao sản phẩm (Bắt buộc).
* **Hành động:** Chạm nút "CONFIRM ORDER" để hoàn tất đơn hàng.

### 2.5.4. Kết quả đầu ra (Phản hồi của ứng dụng)
* **Đơn hàng COD thành công:** Chuyển ngay đến màn hình đặt hàng thành công.
* **Đơn hàng VNPay:** Tự động mở màn hình trình duyệt của Cổng thanh toán VNPay.
* **Lỗi thông tin:** Hiển thị cảnh báo ngay tại các ô nhập liệu bị bỏ sót hoặc hiện thông báo lỗi thanh toán nếu giao dịch gặp sự cố.

---

## 2.5a. Màn hình Cổng thanh toán VNPay (VNPay Payment Screen)

### 2.5a.1. Mục đích
Hiển thị trang giao dịch của cổng thanh toán trực tuyến VNPay dưới dạng một cửa sổ trình duyệt an toàn tích hợp ngay trong ứng dụng, giúp khách hàng hoàn tất thanh toán hóa đơn bằng tài khoản ngân hàng.

### 2.5a.2. Quy trình hoạt động của màn hình
1. **Mở trình duyệt VNPay:** Khi khách hàng chọn thanh toán qua VNPay, ứng dụng sẽ mở ra một cửa sổ hiển thị trang web thanh toán chính thức của VNPay.
2. **Thực hiện giao dịch:** Khách hàng làm theo các hướng dẫn trên giao diện của VNPay:
   - Chọn ngân hàng muốn sử dụng để thanh toán.
   - Quét mã QR bằng ứng dụng ngân hàng hoặc nhập thông tin thẻ/tài khoản ngân hàng của mình.
   - Nhập mã xác thực OTP được ngân hàng gửi về số điện thoại cá nhân để hoàn tất giao dịch.
3. **Theo dõi trạng thái:** Hệ thống của ứng dụng sẽ tự động theo dõi tiến trình thanh toán của khách hàng thông qua kiểm tra địa chỉ trang web đang chạy.
4. **Nhận kết quả giao dịch:** Sau khi hoàn thành các bước xác nhận thanh toán thành công trên trang web của VNPay, trang web sẽ tự động chuyển hướng trang và gửi kết quả giao dịch trở lại ứng dụng. Cửa sổ trình duyệt VNPay tự động đóng lại để đưa khách hàng trở về giao diện ứng dụng.
5. **Hủy thanh toán giữa chừng:** Khách hàng có thể nhấn nút đóng (X) ở góc trên cùng của màn hình nếu không muốn tiếp tục thanh toán. Hệ thống sẽ hiển thị một thông báo xác nhận: "Bạn có chắc muốn hủy thanh toán VNPay? Đơn hàng vẫn ở trạng thái chờ thanh toán.". Nếu khách hàng chọn "Hủy", cửa sổ VNPay sẽ đóng lại và đưa người dùng quay lại trang Thanh toán để lựa chọn lại phương thức khác hoặc thử lại.

### 2.5a.3. Thông tin đầu vào (Dữ liệu khách hàng nhập)
* **Thông tin giao dịch:** Các thao tác nhập thông tin tài khoản ngân hàng, mã OTP trực tiếp trên giao diện trang web VNPay.
* **Hành động hủy:** Nhấn nút đóng (X) ở góc trái và xác nhận hủy.

### 2.5a.4. Kết quả đầu ra (Phản hồi của ứng dụng)
* **Tự động đóng trình duyệt:** Trả kết quả giao dịch chứa thông tin giao dịch về cho màn hình Thanh toán xử lý tiếp.

---

## 2.5b. Màn hình Kết quả Thanh toán (Payment Result Screen)

### 2.5b.1. Mục đích
Hiển thị thông báo chính thức và trực quan về trạng thái của đơn hàng vừa thực hiện thanh toán online (thành công hay thất bại) sau khi khách hàng hoàn tất thao tác tại cổng VNPay.

### 2.5b.2. Quy trình hoạt động của màn hình
1. **Hiển thị kết quả:** Khách hàng được tự động đưa đến màn hình này ngay sau khi trình duyệt thanh toán VNPay đóng lại.
2. **Giao diện kết quả:**
   - **Giao dịch thành công:** Hiển thị biểu tượng dấu tích xanh tròn lớn kèm tiêu đề nổi bật "Thanh toán thành công" và thông tin xác nhận đơn hàng đang được xử lý.
   - **Giao dịch thất bại:** Hiển thị biểu tượng dấu chấm than đỏ kèm tiêu đề "Thanh toán thất bại" và dòng tin nhắn mô tả nguyên nhân lỗi cụ thể (ví dụ: tài khoản không đủ số dư, nhập sai mã xác thực hoặc giao dịch bị hủy bởi người dùng).
3. **Quay lại mua sắm:** Khách hàng nhấn nút "VỀ TRANG CHỦ" ở cuối màn hình để trở lại giao diện chính của ứng dụng và tiếp tục xem sản phẩm.

### 2.5b.3. Thông tin đầu vào (Dữ liệu nhận từ hệ thống)
* **success:** Trạng thái thành công hoặc thất bại của đơn hàng nhận từ ngân hàng (Bắt buộc).
* **message:** Thông tin mô tả chi tiết kết quả (Bắt buộc).
* **Thao tác người dùng:** Nhấn nút "VỀ TRANG CHỦ".

### 2.5b.4. Kết quả đầu ra (Phản hồi của ứng dụng)
* **Giao diện trực quan:** Hiển thị nội dung thông tin đơn hàng tương ứng với kết quả giao dịch.
* **Điều hướng:** Đưa người dùng quay lại trang chủ mua sắm của ứng dụng.

---

## 2.6. Màn hình Thông báo (Notifications Screen)

### 2.6.1. Mục đích
Giúp khách hàng nhận và theo dõi các tin nhắn cập nhật về trạng thái đơn hàng (ví dụ: "Đơn hàng đã được xác nhận", "Đơn hàng đang được giao"), các thông tin ưu đãi, khuyến mãi từ cửa hàng.

### 2.6.2. Quy trình hoạt động của màn hình
1. **Hiển thị thông báo:** Khi truy cập màn hình Thông báo, hệ thống tải toàn bộ các thông tin mới nhất gửi tới tài khoản của khách hàng. Để tối ưu trải nghiệm người dùng, danh sách thông báo hiện tại được mô phỏng trực tiếp trên điện thoại của khách hàng (lưu trữ trong bộ nhớ tạm thời của ứng dụng).
2. **Phân loại trạng thái tin nhắn:** Để giúp khách hàng dễ dàng theo dõi, các thông báo được chia làm hai khu vực rõ rệt:
   - **MỚI NHẤT (Thông báo chưa đọc):** Hiển thị ở trên đầu, có vạch xanh dương nổi bật bên cạnh và chấm tròn nhỏ màu xanh dương báo hiệu tin nhắn mới.
   - **TRƯỚC ĐÓ (Thông báo đã đọc):** Hiển thị ở dưới, màu chữ mờ hơn (độ mờ nhẹ) và không còn chấm tròn màu xanh.
3. **Đọc tin nhắn:** Khách hàng nhấn vào một thông báo chưa đọc bất kỳ để xem nội dung. Hệ thống sẽ tự động chuyển trạng thái thông báo đó thành "Đã đọc", làm mờ chấm xanh đi.
4. **Đọc nhanh tất cả:** Khách hàng có thể nhấn nút "Đánh dấu đã đọc" ở góc trên bên phải màn hình. Hệ thống sẽ tự động cập nhật toàn bộ danh sách thông báo chưa đọc thành đã đọc ngay lập tức và hiện thông báo xác nhận nhỏ ở cuối trang.

### 2.6.3. Thông tin đầu vào (Dữ liệu khách hàng chọn)
* **Xem tin nhắn:** Chạm vào một thông báo cụ thể để đọc chi tiết.
* **Đọc tất cả:** Nhấn nút "Đánh dấu đã đọc" trên thanh công cụ phía trên.

### 2.6.4. Kết quả đầu ra (Phản hồi của ứng dụng)
* **Giao diện cập nhật:** Danh sách thông báo thay đổi màu sắc và ẩn chấm xanh tương ứng sau khi khách hàng bấm đọc.
* **Hộp thư trống:** Hiển thị biểu tượng chiếc chuông im lặng kèm dòng chữ "Không có thông báo" nếu khách hàng hiện không có tin nhắn nào.

---

## 2.7. Màn hình Bản đồ – Vị trí Cửa hàng (Map – Store Location Screen)

### 2.7.1. Mục đích
Cung cấp bản đồ mô phỏng đường đi chi tiết và thông tin liên hệ chính thức của Showroom Sport Pro giúp khách hàng dễ dàng tìm kiếm đường đi và ghé thăm mua sắm trực tiếp tại cửa hàng.

### 2.7.2. Quy trình hoạt động của màn hình
1. **Tích hợp màn hình:** Màn hình này được hiển thị dưới dạng một tab chuyên biệt tên là 'Cửa hàng' nằm trong thanh công cụ bên dưới của trang Quản trị ứng dụng.
2. **Hiển thị bản đồ:** Khi khách hàng bấm vào mục này, màn hình sẽ mở ra giao diện bản đồ đường phố mô phỏng được vẽ tay nghệ thuật. Bản đồ vẽ đầy đủ các tuyến đường lớn, công viên và sông ngòi xung quanh vị trí cửa hàng giúp người dùng dễ dàng định vị phương hướng mà không cần kết nối mạng.
3. **Ghim vị trí cửa hàng:** Ở chính giữa bản đồ hiển thị một kim ghim định vị lớn màu xanh thương hiệu nảy nhẹ xuống để biểu thị vị trí chính xác của Showroom Sport Pro.
4. **Điều chỉnh góc nhìn bản đồ:** Khách hàng có thể sử dụng các nút chức năng ở góc phải màn hình:
   - **Phóng to (+):** Nhấn để nhìn rõ hơn các tuyến đường nhỏ xung quanh cửa hàng (tối đa 1.6 lần).
   - **Thu nhỏ (-):** Nhấn để thu nhỏ, xem toàn cảnh khu vực quận huyện (tối thiểu 0.7 lần).
   - **Đặt lại (Reset):** Đưa bản đồ về tỷ lệ mặc định (1.0) và căn giữa vị trí cửa hàng.
5. **Bảng thông tin liên hệ:** Một thẻ thông tin màu trắng nằm đè ở phía dưới bản đồ hiển thị các thông tin hữu ích và chính xác của cửa hàng:
   - **Tên cửa hàng:** Sport Pro Showroom.
   - **Địa chỉ:** 123 Nguyễn Văn Linh, Quận 7, TP. Hồ Chí Minh.
   - **Điện thoại hỗ trợ:** 0909 123 456.
   - **Thời gian hoạt động:** Từ 08:00 đến 21:00 hàng ngày (kể cả Thứ Bảy và Chủ Nhật).

### 2.7.3. Thông tin đầu vào (Dữ liệu khách hàng chọn)
* **Điều chỉnh bản đồ:** Nhấn các nút (+), (-) hoặc Reset góc nhìn bản đồ.

### 2.7.4. Kết quả đầu ra (Phản hồi của ứng dụng)
* **Giao diện trực quan:** Bản đồ phóng to/thu nhỏ mượt mà theo thao tác bấm và bảng địa chỉ liên hệ của cửa hàng hiển thị rõ nét, dễ đọc.

---

## 2.8. Màn hình Nhắn tin / Trò chuyện (Messaging / Chat Screen)

### 2.8.1. Mục đích
Cung cấp kênh trò chuyện trực tiếp giúp khách hàng nhắn tin thảo luận trực tiếp với đội ngũ tư vấn viên của Sport Pro để được tư vấn thiết kế in ấn sản phẩm hoặc giải đáp các thắc mắc về đơn hàng.

### 2.8.2. Quy trình hoạt động của màn hình
1. **Mở khung chat:** Khi khách hàng bấm vào biểu tượng nhắn tin, ứng dụng sẽ mở ra giao diện phòng chat và tự động hiển thị lịch sử các tin nhắn cũ giữa khách hàng và cửa hàng.
2. **Phân biệt người gửi tin nhắn:** Các tin nhắn được sắp xếp theo trình tự thời gian và phân loại màu sắc rõ ràng để tránh nhầm lẫn:
   - **Tin nhắn của khách hàng:** Khung chat màu xanh dương nổi bật nằm sát lề bên phải màn hình.
   - **Tin nhắn phản hồi từ cửa hàng:** Khung chat màu xám nằm sát lề bên trái màn hình, đi kèm ảnh đại diện tròn của nhân viên tư vấn.
3. **Gửi tin nhắn mới:** Khách hàng nhập nội dung tin nhắn vào ô soạn thảo ở dưới cùng và nhấn nút Gửi (hoặc nhấn nút Enter trên bàn phím). Tin nhắn của khách hàng xuất hiện ngay lập tức trong khung chat và màn hình tự động cuộn xuống dưới cùng.
4. **Lời chào tự động từ hệ thống:** Khi khách hàng bắt đầu một cuộc hội thoại mới lần đầu tiên, hệ thống sẽ tự động gửi một tin nhắn chào mừng ("Chào bạn! Sport Pro có thể giúp gì cho bạn hôm nay?") để tiếp đón khách hàng.
5. **Truyền nhận tin nhắn thời gian thực:** Mọi tin nhắn trao đổi tiếp theo giữa khách hàng và nhân viên hỗ trợ (Admin) được truyền và nhận tức thời theo thời gian thực thông qua kết nối mạng liên tục (WebSocket).
6. **Gửi hình ảnh đính kèm:** Khách hàng có thể nhấn vào nút dấu (+) ở góc dưới bên trái ô nhập liệu để chọn và gửi các file hình ảnh bản vẽ thiết kế hoặc logo mong muốn in ấn từ bộ sưu tập điện thoại của mình.

### 2.8.3. Thông tin đầu vào (Dữ liệu khách hàng nhập)
* **Nội dung tin nhắn:** Nội dung văn bản khách hàng gõ vào ô nhập liệu (Bắt buộc).
* **Hình ảnh đính kèm:** Ảnh thiết kế chọn từ bộ nhớ máy (Không bắt buộc).
* **Hành động:** Chạm nút gửi tin nhắn.

### 2.8.4. Kết quả đầu ra (Phản hồi của ứng dụng)
* **Hiển thị mượt mà:** Tin nhắn của khách hàng và phản hồi của tư vấn viên (Admin) được cập nhật liên tục vào cuộc hội thoại.
* **Cuộn trang tự động:** Màn hình tự động cuộn xuống cuối cùng mỗi khi có tin nhắn mới xuất hiện giúp cuộc trò chuyện không bị gián đoạn.
