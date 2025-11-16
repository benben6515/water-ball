import NextAuth, { NextAuthOptions } from 'next-auth';
import GoogleProvider from 'next-auth/providers/google';
import FacebookProvider from 'next-auth/providers/facebook';

/**
 * NextAuth.js configuration for OAuth authentication.
 *
 * Providers:
 * - Google OAuth 2.0
 * - Facebook Login
 *
 * Strategy:
 * - JWT-based sessions (no database sessions)
 * - 7-day session expiry
 * - Tokens stored in HTTP-only cookies
 *
 * Flow:
 * 1. User clicks "使用 Google 登入" / "使用 Facebook 登入"
 * 2. Redirected to OAuth provider consent screen
 * 3. After consent, callback to backend /auth/oauth/{provider}/callback
 * 4. Backend creates/merges user, returns JWT
 * 5. Frontend stores JWT and establishes session
 */
export const authOptions: NextAuthOptions = {
  providers: [
    // Google OAuth Provider
    GoogleProvider({
      clientId: process.env.GOOGLE_CLIENT_ID!,
      clientSecret: process.env.GOOGLE_CLIENT_SECRET!,
      authorization: {
        params: {
          prompt: 'consent',
          access_type: 'offline',
          response_type: 'code',
        },
      },
    }),

    // Facebook OAuth Provider
    FacebookProvider({
      clientId: process.env.FACEBOOK_CLIENT_ID!,
      clientSecret: process.env.FACEBOOK_CLIENT_SECRET!,
      authorization: {
        params: {
          scope: 'email,public_profile',
        },
      },
    }),
  ],

  // Session configuration
  session: {
    strategy: 'jwt',
    maxAge: 7 * 24 * 60 * 60, // 7 days in seconds
  },

  // JWT configuration
  jwt: {
    maxAge: 7 * 24 * 60 * 60, // 7 days
  },

  // Custom pages
  pages: {
    signIn: '/login',
    error: '/auth/error',
  },

  // Callbacks
  callbacks: {
    /**
     * JWT callback - called whenever JWT is created or updated.
     * Used to add custom claims to JWT.
     */
    async jwt({ token, account, profile }) {
      // On initial sign in, account and profile are available
      if (account && profile) {
        token.accessToken = account.access_token;
        token.provider = account.provider;
        token.profile = profile;
      }
      return token;
    },

    /**
     * Session callback - called whenever session is checked.
     * Adds data from JWT to session object available on client.
     */
    async session({ session, token }) {
      if (token && token.sub) {
        session.user = {
          ...session.user,
          id: token.sub,
          provider: token.provider || '',
        };
      }
      return session;
    },

    /**
     * Sign in callback - called after successful OAuth.
     * Redirects to backend for account creation/linking.
     */
    async signIn({ account, profile }) {
      if (account && profile) {
        // Redirect to backend OAuth callback
        // Backend will handle user creation/merging
        const backendUrl = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
        const callbackUrl = `${backendUrl}/auth/oauth/${account.provider}/callback`;

        // Store OAuth data for backend callback
        // (In production, use encrypted session storage)
        if (typeof window !== 'undefined') {
          sessionStorage.setItem('oauth_account', JSON.stringify(account));
          sessionStorage.setItem('oauth_profile', JSON.stringify(profile));
        }
      }
      return true;
    },

    /**
     * Redirect callback - controls where user goes after sign in.
     */
    async redirect({ url, baseUrl }) {
      // Redirect to profile page after successful sign in
      if (url.startsWith(baseUrl)) {
        return url;
      } else if (url.startsWith('/')) {
        return `${baseUrl}${url}`;
      }
      return `${baseUrl}/profile`;
    },
  },

  // Enable debug logging in development
  debug: process.env.NODE_ENV === 'development',
};

const handler = NextAuth(authOptions);

export { handler as GET, handler as POST };
