import type { UserRole, RoleMetadata } from '@/types/api';

/**
 * Role metadata mapping.
 * Matches the backend UserRole enum metadata.
 */
const ROLE_METADATA: Record<UserRole, RoleMetadata> = {
  GUEST: {
    displayName: '訪客',
    color: '#6B7280', // gray-500
  },
  STUDENT: {
    displayName: '學生',
    color: '#3B82F6', // blue-500
  },
  TEACHER: {
    displayName: '老師',
    color: '#8B5CF6', // purple-500
  },
  ADMIN: {
    displayName: '管理員',
    color: '#EF4444', // red-500
  },
};

/**
 * Get role metadata for display.
 * @param role User role
 * @returns Role metadata with display name and color
 */
export function getRoleMetadata(role: UserRole): RoleMetadata {
  return ROLE_METADATA[role];
}

/**
 * Get Tailwind CSS color classes for role badge.
 * @param role User role
 * @returns Object with background and text color classes
 */
export function getRoleColorClasses(role: UserRole): {
  bg: string;
  text: string;
  border: string;
} {
  switch (role) {
    case 'ADMIN':
      return {
        bg: 'bg-red-100',
        text: 'text-red-800',
        border: 'border-red-300',
      };
    case 'TEACHER':
      return {
        bg: 'bg-purple-100',
        text: 'text-purple-800',
        border: 'border-purple-300',
      };
    case 'STUDENT':
      return {
        bg: 'bg-blue-100',
        text: 'text-blue-800',
        border: 'border-blue-300',
      };
    case 'GUEST':
      return {
        bg: 'bg-gray-100',
        text: 'text-gray-800',
        border: 'border-gray-300',
      };
  }
}

/**
 * Check if user has required role or higher privileges.
 * Role hierarchy: ADMIN > TEACHER > STUDENT > GUEST
 * @param userRole Current user's role
 * @param requiredRole Required role for access
 * @returns true if user has sufficient privileges
 */
export function hasRoleAccess(userRole: UserRole, requiredRole: UserRole): boolean {
  const hierarchy: Record<UserRole, number> = {
    GUEST: 0,
    STUDENT: 1,
    TEACHER: 2,
    ADMIN: 3,
  };

  return hierarchy[userRole] >= hierarchy[requiredRole];
}
