import Navbar from '@/components/Navbar';

export default function DashboardLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="min-h-screen flex flex-col">
      <Navbar />
      <main className="flex-1 p-4 sm:p-6 lg:p-8 w-full max-w-7xl mx-auto">
        {children}
      </main>
    </div>
  );
}
