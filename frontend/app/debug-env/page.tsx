'use client';

export default function DebugEnvPage() {
  // Try multiple ways to access the env var
  const methods = {
    'process.env.NEXT_PUBLIC_BACKEND_URL': typeof process !== 'undefined' ? process.env.NEXT_PUBLIC_BACKEND_URL : 'process is undefined',
    'Direct process check': typeof process !== 'undefined' ? 'process exists' : 'process does NOT exist',
    'window location': typeof window !== 'undefined' ? window.location.href : 'window undefined',
  };

  return (
    <div style={{ padding: '20px', fontFamily: 'monospace' }}>
      <h1>Environment Debug Page</h1>
      <p>This page helps debug environment variable issues</p>

      <h2>Build Time: {new Date().toISOString()}</h2>

      <h2>Environment Variable Tests:</h2>
      <pre style={{ background: '#f0f0f0', padding: '10px', borderRadius: '5px' }}>
        {JSON.stringify(methods, null, 2)}
      </pre>

      <h2>All process.env (if available):</h2>
      <pre style={{ background: '#f0f0f0', padding: '10px', borderRadius: '5px' }}>
        {typeof process !== 'undefined'
          ? JSON.stringify(process.env, null, 2)
          : 'process.env is not available in browser'
        }
      </pre>
    </div>
  );
}
