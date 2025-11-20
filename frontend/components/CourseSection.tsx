'use client';

import { useRouter } from 'next/navigation';
import coursesData from '@/data/courses.json';

interface Course {
  id: number;
  title: string;
  description: string;
  instructor: string;
  duration: string;
  level: string;
  price: number | string;
  comingSoon: boolean;
  features: string[];
}

export default function CourseSection() {
  const router = useRouter();
  const courses: Course[] = coursesData;

  return (
    <section className="py-16 px-6">
      <div className="container mx-auto max-w-4xl">
        <h2 className="text-2xl font-bold mb-12">課程</h2>

        <div className="space-y-8">
          {courses.map((course) => (
            <div
              key={course.id}
              onClick={() => !course.comingSoon && router.push(`/courses/${course.id}`)}
              className={`border border-amber-200 rounded-lg p-6 hover:shadow-md transition-all bg-white relative ${
                course.comingSoon
                  ? 'opacity-75 cursor-not-allowed'
                  : 'hover:border-green-400 cursor-pointer'
              }`}
            >
              {course.comingSoon && (
                <div className="absolute top-4 right-4 bg-orange-500 text-white px-3 py-1 rounded-full text-xs font-semibold">
                  即將推出
                </div>
              )}

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
                  {typeof course.price === 'number'
                    ? `NT$ ${course.price.toLocaleString()}`
                    : `NT$ ${course.price}`}
                </span>
                {!course.comingSoon ? (
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      router.push(`/courses/${course.id}`);
                    }}
                    className="px-6 py-2 bg-green-600 text-white rounded hover:bg-green-700 transition-colors shadow-sm hover:shadow-md"
                  >
                    查看課程
                  </button>
                ) : (
                  <button
                    disabled
                    className="px-6 py-2 bg-gray-400 text-white rounded cursor-not-allowed shadow-sm"
                  >
                    即將推出
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
}
