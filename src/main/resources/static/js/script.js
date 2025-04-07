document.addEventListener('DOMContentLoaded', function() {
    const form = document.querySelector('form');
    const input = document.getElementById('content');
    const charCount = document.getElementById('charCount');
    const charCounter = document.querySelector('.char-counter');
    const maxMessageLength = 30;

    // 입력 가능한 문자 패턴 (한글, 영어, 숫자만)
    const allowedPattern = /^[ㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9\s]*$/;

    // 글자 수 업데이트 함수
    function updateCharCount() {
        const text = input.value;
        let count = 0;
        for (let i = 0; i < text.length; i++) {
            const char = text.charAt(i);
            if (/[ㄱ-ㅎㅏ-ㅣ가-힣]/.test(char)) {
                count += 2; // 한글은 2자로 카운트
            } else {
                count += 1; // 나머지는 1자로 카운트
            }
        }

        charCount.textContent = count;

        if (count > maxMessageLength) {
            charCounter.classList.add('limit-reached');
            input.value = text.substring(0, determineMaxLength(text));
            updateCharCount();
        } else {
            charCounter.classList.remove('limit-reached');
        }
    }

    // 최대 입력 가능 길이 계산
    function determineMaxLength(text) {
        let count = 0;
        for (let i = 0; i < text.length; i++) {
            const char = text.charAt(i);
            if (/[ㄱ-ㅎㅏ-ㅣ가-힣]/.test(char)) {
                count += 2;
            } else {
                count += 1;
            }

            if (count > maxMessageLength) {
                return i;
            }
        }
        return text.length;
    }

    input.addEventListener('input', function(e) {
        const text = e.target.value;
        if (!allowedPattern.test(text)) {
            e.target.value = text.replace(/[^\sㄱ-ㅎㅏ-ㅣ가-힣a-zA-Z0-9]/g, '');
        }

        updateCharCount();
    });

    form.addEventListener('submit', function(e) {
        if (parseInt(charCount.textContent) > maxMessageLength) {
            e.preventDefault();
            return;
        }
    });

    updateCharCount();
});