import CourseSection from '@/components/CourseSection';
import InstructorSection from '@/components/InstructorSection';
import SocialMediaSection from '@/components/SocialMediaSection';
import Footer from '@/components/Footer';

export default function Home() {
  return (
    <div className="min-h-screen bg-gradient-to-b from-amber-50/50 to-white">
      {/* Hero Section */}
      <section className="py-20 px-6 bg-gradient-to-br from-amber-100/30 via-orange-50/20 to-amber-50/30">
        <div className="container mx-auto text-center max-w-2xl">
          <h1 className="text-4xl font-bold mb-4 text-green-700">
            地球軟體學院
          </h1>
          <p className="text-lg text-gray-600">
            培養扎實的軟體工程師
          </p>
        </div>
      </section>

      <CourseSection />
      <InstructorSection />
      <SocialMediaSection />
      <Footer />
    </div>
  );
}
