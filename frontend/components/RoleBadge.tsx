import type { UserRole } from '@/types/api';
import { getRoleMetadata, getRoleColorClasses } from '@/lib/roleUtils';

interface RoleBadgeProps {
  role: UserRole;
  size?: 'sm' | 'md' | 'lg';
  variant?: 'solid' | 'outline';
}

/**
 * Discord-style role badge component.
 * Displays user role with color-coded styling.
 *
 * @param role - User role (GUEST, STUDENT, TEACHER, ADMIN)
 * @param size - Badge size (default: 'sm')
 * @param variant - Badge style variant (default: 'solid')
 */
export default function RoleBadge({ role, size = 'sm', variant = 'solid' }: RoleBadgeProps) {
  const metadata = getRoleMetadata(role);
  const colors = getRoleColorClasses(role);

  // Size classes
  const sizeClasses = {
    sm: 'text-xs px-2 py-0.5',
    md: 'text-sm px-3 py-1',
    lg: 'text-base px-4 py-1.5',
  };

  // Variant classes
  const variantClasses = variant === 'solid'
    ? `${colors.bg} ${colors.text}`
    : `bg-transparent ${colors.text} border ${colors.border}`;

  return (
    <span
      className={`
        inline-flex items-center justify-center
        font-medium rounded-full
        ${sizeClasses[size]}
        ${variantClasses}
        transition-colors duration-200
      `}
      title={`角色: ${metadata.displayName}`}
    >
      {metadata.displayName}
    </span>
  );
}
