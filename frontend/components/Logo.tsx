export default function Logo({ className = "" }: { className?: string }) {
  return (
    <svg
      viewBox="0 0 100 100"
      className={className}
      xmlns="http://www.w3.org/2000/svg"
    >
      {/* 海洋背景 - 綠藍色 */}
      <circle cx="50" cy="50" r="45" fill="#059669" />

      {/* 土地區塊 - 土色 */}
      <path
        d="M 30 25 Q 35 20, 45 22 T 65 28 Q 70 32, 68 40 L 60 45 Q 55 48, 50 46 L 42 42 Q 35 38, 30 35 Z"
        fill="#92400E"
      />

      <path
        d="M 20 55 Q 25 50, 35 52 L 42 55 Q 48 58, 45 65 L 38 68 Q 30 68, 25 65 Z"
        fill="#92400E"
      />

      <path
        d="M 60 60 Q 68 58, 75 62 L 78 68 Q 76 74, 70 75 L 62 73 Q 58 68, 60 60 Z"
        fill="#92400E"
      />

      {/* 綠色植被點綴 */}
      <circle cx="38" cy="32" r="3" fill="#10B981" opacity="0.7" />
      <circle cx="48" cy="35" r="2" fill="#10B981" opacity="0.7" />
      <circle cx="32" cy="60" r="2.5" fill="#10B981" opacity="0.7" />

      {/* 高光效果 */}
      <circle cx="35" cy="30" r="8" fill="rgba(255, 255, 255, 0.2)" />

      {/* 外框 - 綠色 */}
      <circle
        cx="50"
        cy="50"
        r="45"
        fill="none"
        stroke="#047857"
        strokeWidth="2"
      />
    </svg>
  );
}
