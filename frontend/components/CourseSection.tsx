import coursesData from '@/data/courses.json';

interface Course {
  id: number;
  title: string;
  description: string;
  instructor: string;
  duration: string;
  level: string;
  price: number;
  features: string[];
}

export default function CourseSection() {
  const courses: Course[] = coursesData;

  return (
    <section className="py-16 px-6">
      <div className="container mx-auto max-w-4xl">
        <h2 className="text-2xl font-bold mb-12">課程</h2>

        <div className="space-y-8">
          {courses.map((course) => (
            <div
              key={course.id}
              className="border border-amber-200 rounded-lg p-6 hover:shadow-md hover:border-green-400 transition-all bg-white"
            >
              <h3 className="text-xl font-semibold mb-2">
                {course.title}
              </h3>
              <p className="text-gray-600 mb-4">{course.description}</p>

              <div className="flex gap-4 text-sm text-gray-500 mb-4">
                <span>{course.instructor}</span>
                <span>•</span>
                <span>{course.duration}</span>
                <span>•</span>
                <span>{course.level}</span>
              </div>

              <div className="flex items-center justify-between pt-4 border-t">
                <span className="text-xl font-semibold">
                  NT$ {course.price.toLocaleString()}
                </span>
                <button className="px-6 py-2 bg-green-600 text-white rounded hover:bg-green-700 transition-colors shadow-sm hover:shadow-md">
                  購買
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
