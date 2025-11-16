import instructorsData from '@/data/instructors.json';

interface Instructor {
  id: number;
  name: string;
  title: string;
  bio: string;
  expertise: string[];
}

export default function InstructorSection() {
  const instructors: Instructor[] = instructorsData;

  return (
    <section className="py-16 px-6 bg-amber-50">
      <div className="container mx-auto max-w-4xl">
        <h2 className="text-2xl font-bold mb-12">講師</h2>

        <div className="space-y-8">
          {instructors.map((instructor) => (
            <div key={instructor.id}>
              <h3 className="text-xl font-semibold mb-1">{instructor.name}</h3>
              <p className="text-sm text-gray-500 mb-4">{instructor.title}</p>
              <p className="text-gray-700 leading-relaxed">{instructor.bio}</p>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
